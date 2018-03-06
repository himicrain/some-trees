

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
/*
class Data{
	public char letter;
	public int weight ;
	
	public Data(){
		this.letter = ' ';
		this.weight = -1;
	}
	public Data(char letter, int weight){
		this.letter = letter;
		this.weight = weight;
	}
}*/

import javax.xml.namespace.QName;

class Node{
	public int weight;
	public char letter;
	public Node leftChild;
	public Node rightChild;
	public Node parent;
	
	public Node(){
		this.letter = ' ';
		this.weight = -1;
		this.leftChild = null;
		this.rightChild = null;
		this.parent = null;
		
	}
	public Node(char letter, int weight){
		this.weight = weight;
		this.letter = letter;
		this.leftChild = null;
		this.rightChild = null;
		this.parent = null;
	}
}


public class Huffman {
	
	//public Data[] Datas;
	public Node[] Nodes;
	public int Valid;
	
	
	public Huffman(String filename) throws IOException{
		
		char [] Letters = new char[255];
		int [] Weights = new int[255];
		for(int i=0;i<255;i++){
			Letters[i] = ' ';
			Weights[i] = 0;
		}
		
		//读取资源文件，分析每个字符出现的次数，然后进行统计
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		
		//每次读取一行
		String line = br.readLine();
		int num = 0;
		while(line != null){
			//将每一行的字符分别提取出来，并进行统计出现次数
			for(int i=0;i<line.length();i++){
				char ch = line.charAt(i);
				int pos =-1;
				
				//查找该字符是否在Letter数组里，如果在pos指向所在数组的角标，如果不在则为-1
				for(int j=0;j<num;j++){
					if(Letters[j] == ch){
						pos = j;
					}
				}
				//如果pos == -1说明该字符还没有出现过
				if(pos == -1){
					Letters[num] = ch;
					Weights[num] ++ ;
					num ++;
				}else{
					//否则的话更新该位置Weights数组值，加一
					Weights[pos] ++ ;			
				}
			}
			//读取下一行
			line = br.readLine();
		}
		
		br.close();
		
		System.out.println(num);
		//Valid 表示当前数组中存储的字符数量 。 注： weights数组和letters数组是同步的，就是letters对应角标，在weights数组中是该字符的出现次数
		Valid = num;
		
		//数学证明可得，树的所有节点总数为2*Valid -1个
		this.Nodes = new Node[2*Valid - 1];
		int currentPos = 0;
		//初始化节点数组， 使用有效字符创建节点
		for(int i=0;i<255;i++){
			// 使用有效字符创建节点，此时创建了Valid个节点
			if(Weights[i] != 0){
				int weight = Weights[i];
				char letter = Letters[i];
				Node tempNode = new Node(letter,weight);
				this.Nodes[currentPos] = tempNode;
				currentPos ++ ;
			}
		}
		System.out.println(this.Nodes.length);
		for(int i=0;i<this.Nodes.length;i++){
			if(this.Nodes[i] != null)
				System.out.println(this.Nodes[i].letter + "  "+ this.Nodes[i].weight);
		}
		
		//将剩余节点初始化
		for(int i=this.Valid;i<2*this.Valid-1;i++){
			Node tempNode = new Node();
			this.Nodes[i] = tempNode;
		}
		
		
		
		
	}
	
	public void CreatTree(){
		
		//根据初始化的节点数组，创建哈夫曼树，从角标值为Valid开始
		for(int i=this.Valid;i<2*this.Valid-1;i++){
			//从节点数组中找到两个weight值最小的两个节点的角标
			int []P = this.getMins(i);
			int p1 = P[0];//两个中较小一个
			int p2 = P[1];//较大一个
			//角标i的节点的左孩子指向找到的较小的节点
			this.Nodes[i].leftChild = this.Nodes[p1];
			//右孩子指向找到的较大的节点
			this.Nodes[i].rightChild = this.Nodes[p2];
			//该新创建的节点的权重weight值为，p1 p2对应节点的weight的和
			this.Nodes[i].weight = this.Nodes[p1].weight + this.Nodes[p2].weight;
			//更新该两个最小节点的父节点，指向i节点
			this.Nodes[p1].parent = this.Nodes[i];
			this.Nodes[p2].parent = this.Nodes[i];
		}
			
	}
	
	//获得最小的两个节点的角标
	public int[] getMins(int n){
		int p1 = 0;//min
		int p2 = 0;//max
		int min = 10000000;
		int max = 10000000;
		//先找到最小的节点
		for(int i=0;i<n;i++){
			if(min > this.Nodes[i].weight && this.Nodes[i].parent == null ){
				p1 = i;
				min = this.Nodes[i].weight;
			}
		}
		//找到除p1外最小的节点
		for(int i=0;i<n;i++){
			if(max > this.Nodes[i].weight && i != p1 && this.Nodes[i].parent == null){
				p2 = i;
				max = this.Nodes[i].weight;
			}
		}
		
		//返回两个节点对应的角标
		int []P = new int[2];
		P[0] = p1;
		P[1] = p2;
		
		return P;
		
	}
	
	//获得到每个字符的哈夫曼编码，递归实现
	public String[] getEncodingString(){
		
		Node root = this.Nodes[2*this.Valid-2];
		String str = "";
		//用于存储所有字符的哈夫曼编码
		String[] encodingStr = new String[this.Valid];
		this.search(root,str,encodingStr);
		
		return encodingStr;

		
	}
	
	//递归搜索每个字符的哈夫曼编码，此处是中序遍历
	private String[] search(Node P_root, String str, String[] encodingString){
		
		//如果左子树不为空，那么搜索左子树
		if(P_root.leftChild != null){
			encodingString = this.search(P_root.leftChild, str+"0", encodingString);
			encodingString = this.search(P_root.rightChild, str+"1", encodingString);
		}else if(P_root.rightChild != null){
			//如果右子树不为空，那么搜索右子树
			encodingString = this.search(P_root.rightChild, str+"1", encodingString);
			//然后搜索左子树
			encodingString = this.search(P_root.leftChild, str+"0", encodingString);
		}else{
			//搜索到的话，将得到的字符串存储到数组中
			char ch = P_root.letter;
			int pos =0;
			for(int k=0;k<this.Valid;k++){
				if(this.Nodes[k].letter == ch){
					pos = k;
					break;
					
				}
			}
			encodingString[pos] = str;
		}
		//返回存储数组
		return encodingString;
	}
	
	

	public static void main(String[] args) throws IOException{
		
		/*
		try {
			if(args[0] == ""){
				System.out.println("no file input or no such file ");
			}
		} catch (Exception e) {
			System.out.println("no file input or no such file ");
			return ;
		}
		
		Huffman ht = new Huffman(args[0]);*/
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("input the source file ");
		
		String path = sc.nextLine();
		
		Huffman ht = new Huffman(path);
		//HTree ht = new HTree("/home/micrain/workspace/Trees/src/data.txt");
		
		ht.CreatTree();
		String[] S = ht.getEncodingString();
		
		for(int i=0;i<S.length;i++){
			System.out.println("the letter " + ht.Nodes[i].letter + "'s huffman coding is   " + S[i]);
		}
		
		System.out.println("input the random strings file");
		String idPath = sc.nextLine();
		
		Scanner in = new Scanner(System.in);
		//循环输入需要编码的字符串
		while(true){
			System.out.println("enter your id , enter q for exit");
			String line = in.nextLine();
			
			if(line.length() == 1 && line.charAt(0) == 'q'){
				break;
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(idPath)));
			String l = br.readLine();
		
			while(l!= null){
				String ps = l.substring(0, 8);
				if(ps.equals(line)){
					line = l.substring(8);
				}
				l = br.readLine();
			}
			
			br.close();
			
			/*26648150
			 * /home/micrain/workspace/Trees/src/jabberwock.txt
			 * /home/micrain/workspace/Trees/src/RandomStrings.txt
			 * */	

			System.out.print("the encoding number is : ");
			String encoding = "";//所有字符的哈夫曼编码合并成一个字符串
			//根据输入的字符串的每一个字符，查找对应的哈夫曼编码，解析，合并，输出
			for(int i=0;i<line.length();i++){
				char ch = line.charAt(i);
				for(int j=0;j<S.length;j++){
					if(ht.Nodes[j].letter == ch){
						//System.out.print(S[j] + " ");;
						System.out.print(Integer.parseInt(S[j],2) + " ");;
						encoding += S[j];
					}
				}
			}
			
			System.out.println();
			System.out.println("the binary string is : " + encoding);
			System.out.print("the encoding ASCII code is : ");
			
			int pos = 0;
			//获得到完整字符串编码后得到的ascii码串
			while(true){
				String sub ;
				if(pos+8 > encoding.length()){
					sub = encoding.substring(pos, encoding.length());
				}else{
					sub = encoding.substring(pos, pos+8 );	
				}
				
				System.out.print((char)Integer.parseInt(sub,2));
				pos += 8;
				if(pos >= encoding.length()){
					break;
				}
			}
			
			System.out.println();
			
		}
		
		in.close();
		
	}
	
}