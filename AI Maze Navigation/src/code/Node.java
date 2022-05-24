package code;
public class Node implements Comparable<Node> {
String operator = "";	
int level= 0;
String state="";
Node parent;
int cost = 0;
String hashState;


@Override
public int compareTo(Node o) {
	// TODO Auto-generated method stub
    return o.cost < this.cost? 1 : -1;
}

@Override
public String toString() {
    return "cost:" + this.cost;
}



}