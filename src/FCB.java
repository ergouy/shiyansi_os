import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FCB {
    String name;
    int size;
    int first_block;
    int type;//1为文件，2为目录，0为已删除目录项
    String time;
    FCB fathernode;
    ArrayList<FCB> childlist;
    String context;

    public FCB(String name, int size,int type) {
        this.name = name;
        this.size = size;
        this.type = type;
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.time = sdf.format(d);
        childlist = new ArrayList<>();
        this.context = null;
    }

    @Override
    public boolean equals(Object obj) {
        FCB fcb = (FCB)obj;
        return fcb.name.equals(this.name);
    }

    public void showF(){
        System.out.println("              -------当前目录:"+this.name+"-------           ");
        System.out.println("    文件名\t"+"  \t文件类型\t"+"   \t文件大小\t"+"  \t文件内容"+"   \t\t创建时间");
        for (int i=0; i<this.childlist.size(); ++i){
            if(this.childlist.get(i).type == 1){
                System.out.println(" \t"+this.childlist.get(i).name+"   \t\t文件\t"+" \t\t"+this.childlist.get(i).size+"  \t\t  "+this.childlist.get(i).context+"    \t\t"+this.childlist.get(i).time);
            }else if(this.childlist.get(i).type == 2){
                System.out.println(" \t"+this.childlist.get(i).name+"   \t\t目录\t"+" \t\t"+this.childlist.get(i).size+"  \t\t"+"  null  "+"  \t\t"+this.childlist.get(i).time);
            }
        }
    }
}
