import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

class AVLNode{
	public int data;
	public int height;
	public AVLNode leftChild;
	public AVLNode rightChild;
	
	public AVLNode(int data){
		this.data = data;
		this.height = 0;
		this.leftChild = null;
		this.rightChild = null;
	}
	
	public AVLNode(int data, AVLNode leftChild, AVLNode rightChild){
		this.data = data;
		this.height = 0;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
	}
	
	
}



public class atree {
	
	//保留当前树的根节点
	public AVLNode Root = null;
	
	public long changesTimes = 0;
	public long comparisonsTime = 0;
	
	/*获取当前子树的深度, 
	 * 没有使用到平衡因子，通过深度对比起到平衡因子作用
	 * 
	 * */
	public int getHeight(AVLNode subRoot){
		int height = (subRoot == null ? -1 : subRoot.height);
		return height;
	}
	
	//左单旋(LL)
	private AVLNode leftRotation(AVLNode node){
		
		AVLNode p = node.leftChild;
		node.leftChild = p.rightChild;
		this.changesTimes ++ ;
		p.rightChild = node;
		this.changesTimes ++ ;
		
		int HL = this.getHeight(node.leftChild) ;
		int HR = this.getHeight(node.rightChild);
		node.height = (HL>HR ?  (HL+1) : (HR +1));
		
		int PHL = this.getHeight(p.leftChild);
		p.height = ( PHL > node.height ? (PHL+1) : (node.height +1) );
		
		return p;
	}
	
	//右单旋(RR)
	private AVLNode rightRotation(AVLNode node){
		AVLNode p = node.rightChild;
		node.rightChild = p.leftChild;
		this.changesTimes ++ ;
		p.leftChild = node;
		this.changesTimes ++ ;
		
		int HL = this.getHeight(node.leftChild);
		int HR = this.getHeight(node.rightChild);
		
		node.height = (HL > HR ? (HL+1) : (HR+1));
		int PHR = this.getHeight(p.rightChild);
		int NH = node.height;
		
		p.height = (PHR > NH ? (PHR+1):(NH+1));
		
		return p;
	}
	
	//左双旋 == 该节点的左节点右单旋(RR) && 该节点左单旋(LL)
	private AVLNode leftRightRotation(AVLNode node){
		node.leftChild = this.rightRotation(node.leftChild);
		this.changesTimes ++ ;
		AVLNode p = this.leftRotation(node);
		return p;
	}
	
	//RL型操作，见说明 == LL && RR
	private AVLNode rightLeftRotation(AVLNode node){
		node.rightChild = this.leftRotation(node.rightChild);
		this.changesTimes ++ ;
		AVLNode p = this.rightRotation(node);
		return p;
	}
	
	//找到该节点下的最大值节点
	private AVLNode MAX(AVLNode root){
		if(root == null){
			return null;
		}
		AVLNode P = root;
		while(P.rightChild != null ){
			P = P.rightChild;
		}
		return P;
		
	}
	
	//找到该节点的下的最小值节点，一直循环找左树的左树就行，左树值总是小于右树值
	private AVLNode MIN(AVLNode root){
		AVLNode P = root;
		if(P == null){
			return null;
		}
		while(P.leftChild != null){
			P = P.leftChild;
		}
		return P;
		
	}
	
	
	
	public AVLNode search(AVLNode root, int data){
		
		if(root == null || root.data == data){
			return root;
		}
		/*
		 * 因为binary search tree 的本身性质，左节点data总是小于右节点data，
		 * 所以如果data< root.data 的话，说明要查找的节点在该root节点的左子树上，所以递归搜索左树
		 * 当data >= root.data时同理
		 * 最终递归遍历找到目标节点，返回目标节点
		 * */
		
		this.comparisonsTime ++ ;
		if(data < root.data){
			return this.search(root.leftChild, data);
		}else{
			return this.search(root.rightChild, data);
		}
	}
	public AVLNode insert(AVLNode root, int data){
		
		this.comparisonsTime ++ ;
		//如果节点为空说明找到了要查入位置
		if(root == null){
			root = new AVLNode(data);
			this.changesTimes ++ ;
			this.comparisonsTime --;
		}else if(root.data > data){ 
			//如果root节点的值大于data， 说明data节点在root节点的左树上
			root.leftChild = this.insert(root.leftChild, data);
			
			//根据上面返回的节点，判断当前root节点是否平衡，如果不平衡那么进行均衡操作
			if(this.getHeight(root.leftChild) - this.getHeight(root.rightChild) == 2){
				//左子树深度大于右子树深度，且不均衡 （深度差等于二）
				this.comparisonsTime ++ ;
				if( root.leftChild.data > data){
					//满足LL型
					root = this.leftRotation(root);
				}else{ //满足LR型
					root = this.leftRightRotation(root);
				}
			}
		}else if ( root.data < data) {
			//道理同上
			root.rightChild = this.insert(root.rightChild, data);
			if(this.getHeight(root.rightChild) - this.getHeight(root.leftChild) == 2){
				
				this.comparisonsTime ++ ;
				if(root.rightChild.data >= data){
					//满足RL型
					root = this.rightLeftRotation(root);
				}else {
					//满足RR型
					root = this.rightRotation(root);
				}
			}
			
		}else{
			System.out.println("There is the same node which has the data in the tree ");
		}
		
		//每层递归插入后，都要进行对每层相关节点深度的更新（递归中经过的节点）
		int HL = this.getHeight(root.leftChild);
		int HR = this.getHeight(root.rightChild);
		root.height = (HL > HR ? (HL+1) : (HR+1));
		
		return root;
	}
	
	private AVLNode deleteNode(AVLNode root, AVLNode node){
		if(root == null ){
			return null;
		}
		this.comparisonsTime ++ ;
		if(node.data < root.data){
			
			//如果删除节点值比当前节点值小，那么就在当前节点的左子树上。 相反同理
			root.leftChild = this.deleteNode(root.leftChild, node);
			
			//这一块其实就是检测当前节点下左右子树是否平衡，  因为node 节点在root 的左子树上，所以删除后，只能是左子树比右子树高度低
			//并且，一旦检测到该节点的左右子树不平衡，立即会进行平衡操作（并且该左右子树不平衡的节点一定在树的底层，并且一旦经过如下平衡操作后，该点的上层节点都是均衡的，这一点很重要）
			
			//总的来说就是检测当强节点是否平衡，没有直接使用平衡因子（麻烦），这一步在起到平衡的作用
			if(this.getHeight(root.rightChild) - this.getHeight(root.leftChild) == 2){
				AVLNode R = root.rightChild; // R为目标节点
				//此时已经判断处root节点下左右不平衡，并且右子树深度较大，那么此时 R 节点的左子树深时，满足 RL型
				if(this.getHeight(R.leftChild) > this.getHeight(R.rightChild)){
					root = this.rightLeftRotation(root);
				}else{ // 如果左子树深度较大， 那么满足 RR型
					root = this.rightRotation(root);
				}
			}
		}else if(node.data > root.data){
			
			//道理同上
			root.rightChild = this.deleteNode(root.rightChild, node);
			
			//这一块其实就是检测当前节点下左右子树是否平衡 ， 因为node 节点在root 的右子树上，所以删除后，只能是右子树比左子树高度低 
			if(this.getHeight(root.leftChild)-this.getHeight(root.rightChild) == 2){
				AVLNode L = root.leftChild; // L为目标节点
				if(this.getHeight(L.rightChild) > this.getHeight(L.leftChild)){
					//满足LR型
					root = this.leftRightRotation(root);
				}else {
					//满足LL型
					root = this.leftRotation(root);
				}
			}
		}else{
			
			//如果左右均不空，那么就是左右均有子树的情况， 文档中删除过程部分， 情况3 即是
			if(root.leftChild != null && root.rightChild != null){
				if(this.getHeight(root.leftChild) > this.getHeight(root.rightChild) ){
					AVLNode max = this.MAX(root.leftChild);
					root.data = max.data;
					root.leftChild = this.deleteNode(root.leftChild, max);
					this.changesTimes ++ ;
				}else{
					AVLNode min = this.MIN(root.rightChild);
					root.data = min.data;
					root.rightChild = this.deleteNode(root.rightChild, min);
					this.changesTimes ++ ;
				}
			}else{ // 如果左右至少一个为空， 那么问题得到简化，在文档中 ， 删除过程的 情况1和2
				root = (root.leftChild != null ? root.leftChild : root.rightChild);
			}
		}
		
		//在递归返回过程中，每一个经过的节点的深度深度都会更新
		if(root != null){
			int HL = this.getHeight(root.leftChild);
			int HR = this.getHeight(root.rightChild);
			root.height = (HL > HR ? (HL+1) : (HR+1));
		}
		return root;
	}
	
	//删除操作，使用递归进行删除操作，删除过程中，检测平衡状态，并即时调整
	public AVLNode delete(AVLNode root, int data){
		AVLNode D = this.search(root, data);
		if(D != null){
			root = this.deleteNode(root, D);
		}
		return root;
	}
	
	//前序遍历
	public void preOrder(AVLNode root){
		if(root != null){
			System.out.print(root.data + " ");
			this.preOrder(root.leftChild);
			this.preOrder(root.rightChild);
		}
	}
	//中序遍历
	public void midOrder(AVLNode root){
		if(root != null){
			this.midOrder(root.leftChild);
			System.out.print(root.data + " ");
			this.midOrder(root.rightChild);
		}
		
	}
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		int arr[]= {3,2,1,4,5,6,7,16,15,14,13,12,11,10,8,9};
		//int arr[]= {5,3,6,2,4,7,1};
		

		atree tree = new atree();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
		
		String line = br.readLine();
		
		int numOfFind = 0;
		int numOfAdd = 0;
		int numOfRemove = 0;
		
		while(line != null){
			char OP = line.charAt(0);
			int data = Integer.parseInt(line.trim().substring(1, line.length()));
			
			switch (OP) {
			case 'r':
				numOfRemove ++ ;
				tree.delete(tree.Root, data);
				break;
			case 'a':
				numOfAdd ++ ;
				tree.Root = tree.insert(tree.Root, data);
				break;
			case 'f':
				numOfFind ++ ;
				tree.search(tree.Root, data);
				break;
			}
			line = br.readLine();
			System.out.println(" the number of comparisons       is "+ tree.comparisonsTime + " times");
			System.out.println(" the number of changes           is "+ tree.changesTimes + " times");
			System.out.println(" the number of find operations   is "+ numOfFind + " times");
			System.out.println(" the number of add operations    is "+ numOfAdd + " times");
			System.out.println(" the number of remove operations is "+ numOfRemove + " times");
			System.out.println();
		}
		
		br.close();
		
		/*
		for(int i=0;i<arr.length;i++){
			System.out.println(arr[i]+ " ");
			tree.Root = tree.insert(tree.Root, arr[i]);
			line = br.readLine();
		}*/
		
		tree.preOrder(tree.Root);
		System.out.println();

		tree.midOrder(tree.Root);
		System.out.println();


	}

}
