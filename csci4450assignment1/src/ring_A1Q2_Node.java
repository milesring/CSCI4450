public class ring_A1Q2_Node {

    public String state;
    public int f;
    public int g;
    public int h;


    public ring_A1Q2_Node(){
        f = g = h = 0;
    }

    public ring_A1Q2_Node(String str){
        state = str;
        f = g = h = 0;
    }

}
