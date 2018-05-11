import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Test {
    static final int FATsize = 16;
    static final  int blocksize = 1024;
    static final int EMPTY_BLOCK = 0;//块空标志
    static final int LAST_BLOCK = -1;//文件结束标志
    static FCB catalogue;//全局目录
    static FATNode[] fattable;//FAT表
    static FCB currentdir;//时刻指向当前目录
    static FCB targetdir;//指向目标路径
    static String path;//要现实的当前路径
    static File curfile;
    static void Init(){
        //初始化作用:1.创建根目录 2.创建FAT表 并给根目录分配
        catalogue = new FCB("根目录",1024,2);
        catalogue.fathernode = null;//没有父亲
        fattable = new FATNode[FATsize];
        for(int i=0; i<FATsize; ++i){//初始化FAT表
            FATNode fn = new FATNode(EMPTY_BLOCK,null);
            fattable[i] = fn;
        }
        allocationFat(catalogue);
        currentdir = catalogue;
        path = catalogue.name;
        curfile = new File("D://"+catalogue.name);
        if (!(curfile.exists())){//创建文件
            curfile.mkdir();
        }
        //showFat();
    }

    static List<Integer> findBlock(int neednum){
        List<Integer> list = new ArrayList<>();
        for(int i=0; i<fattable.length; ++i){
            if(fattable[i].nextnode == EMPTY_BLOCK){
                if(neednum <= 0){
                    return list;
                }
                list.add(i);
                --neednum;
            }
        }
        return list;
    }

    static void  allocationFat(FCB fcb){//为文件分配外存(修改FATtable)
        int blocnum = (int) Math.ceil((double) fcb.size/1024);
        int remainder = fcb.size%1024;
        List list = findBlock(blocnum);//返回空闲块的下标数组;
        if(fcb.type == 1){//文件
            if(list.size() == 1){//list的大小为1
                fcb.first_block = (int) list.get(0);
                fattable[(int) list.get(0)].nextnode = LAST_BLOCK;
                fattable[(int) list.get(0)].otherinfo = fcb.name;
            }else{//list的大小大于1
                for(int i=0; i<list.size()-1; ++i){
                    if(i == 0){//首块
                        fcb.first_block = (int) list.get(i);
                    }
                    fattable[(int) list.get(i)].nextnode = (int) list.get(i+1);
                    fattable[(int) list.get(i)].otherinfo = fcb.name;
                }
                //单独处理最后一个
                fattable[(int) list.get(list.size()-1)].nextnode = LAST_BLOCK;
                fattable[(int) list.get(list.size()-1)].otherinfo = fcb.name;
            }

        }else if(fcb.type == 2){//目录
            fcb.first_block = (int) list.get(list.size()-1);
            fattable[(int) list.get(list.size()-1)].nextnode = LAST_BLOCK;
            fattable[(int) list.get(list.size()-1)].otherinfo = fcb.name;
        }else{//已删除文件
        }
    }


static void removeFcb(FCB fcb){//从FAT表中删除某文件
    int poshead = fcb.first_block;
    int posbehind;
    do {
        posbehind = fattable[poshead].nextnode;//记录下一块的下标
        fattable[poshead].nextnode = EMPTY_BLOCK;
        fattable[poshead].otherinfo = null;
        poshead = posbehind;
    }while(poshead != LAST_BLOCK);
}

    static void show(){
        boolean flag = true;
        while(flag){
            Scanner sc = new Scanner(System.in);
            System.out.print(path+"/>$");
            String str = sc.nextLine();//输入的命令及路径
            String[] strs = str.split("[ /]");//经路径拆分
            switch (strs[0]){
                case "md"://创建子目录
                    targetdir = currentdir;
                    for(int i=1; i<strs.length; ++i){
                        if (".".equals(strs[i])){//在本目录下创建目录
                            targetdir = currentdir;
                        }else if("..".equals(strs[i])){//去上层目录创建创建目录
                            targetdir = targetdir.fathernode;
                        }else{
                            FCB fcb = new FCB(strs[i],1024,2);
                            allocationFat(fcb);
                            fcb.fathernode = targetdir;
                            targetdir.childlist.add(fcb);
                            File mdfile = new File(curfile,fcb.name);
                            if(! mdfile.exists()){//创建子目录
                                mdfile.mkdir();
                            }
                        }
                    }
                    //showFat();
                    break;
                case "rd"://删除子目录
                    System.out.print("确定要删除目录"+strs[1]+"吗(Y/N)?:");
                    String choice = sc.nextLine();
                    if ("Y".equals(choice)){
                        FCB rdfcb = new FCB(strs[1],1024,2);
                        if (currentdir.childlist.contains(rdfcb)){//找到了,准备删除
                            rdfcb = currentdir.childlist.get(currentdir.childlist.indexOf(rdfcb));
                            if(rdfcb.childlist.size() > 0){
                                System.out.println("不能删除");
                            }else{
                                removeFcb(rdfcb);
                                currentdir.childlist.remove(rdfcb);
                                File rdfile = new File(curfile,rdfcb.name);
                                if(rdfile.exists()){
                                    rdfile.delete();
                                }
                                rdfcb = null;
                            }
                        }else{
                            System.out.println("子目录不存在");
                        }
                    }else{}
                    //showFat();
                    break;
                case "mk":
                    String mkfcbname = strs[1];
                    int mkfcbsize = Integer.parseInt(strs[2]);
                    FCB mkfcb = new FCB(mkfcbname,mkfcbsize,1);
                    System.out.print("输入文件内容:");
                    String neirong = sc.nextLine();
                    mkfcb.context = neirong;
                    allocationFat(mkfcb);
                    mkfcb.fathernode = currentdir;
                    currentdir.childlist.add(mkfcb);
                    //showFat();
                    break;
                case "del":
                    String delname = strs[1];
                    FCB delfcb = new FCB(delname,1024,1);
                    System.out.print("确定要删除文件"+delfcb.name+"(Y/N)?:");
                    choice = sc.nextLine();
                    if ("Y".equals(choice)){
                        if (currentdir.childlist.contains(delfcb)){
                            delfcb = currentdir.childlist.get(currentdir.childlist.indexOf(delfcb));
                            removeFcb(delfcb);
                            currentdir.childlist.remove(delfcb);
                            delfcb = null;
                            //showFat();
                        }else{
                            System.out.println("文件不存在");
                        }
                    }else {}
                    break;
                case "dir":
                    currentdir.showF();
                    break;
                case "tree":
                    showTree(catalogue);
                    break;
                case "cd":
                    for (int i=1; i<strs.length; ++i){
                        if(".".equals(strs[i])){//

                        }else if("..".equals(strs[i])){//返回上一级
                            currentdir = currentdir.fathernode;
                            curfile = curfile.getParentFile();
                            path = path.substring(0,path.lastIndexOf("/"));
                        }else{
                            FCB cdfcb = new FCB(strs[i],1024,2);
                            if (currentdir.childlist.contains(cdfcb)){
                                //获取真正的文件
                                cdfcb = currentdir.childlist.get(currentdir.childlist.indexOf(cdfcb));
                                if (cdfcb.type == 2){
                                    currentdir = cdfcb;
                                    curfile = new File(curfile,cdfcb.name);
                                    path += "/"+currentdir.name;
                                }else{
                                    System.out.print("请输入修改文件内容:");
                                    String neirong2 = sc.nextLine();
                                    cdfcb.context = neirong2;
                                    Date d = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    cdfcb.time = sdf.format(d);
                                    System.out.println("    文件名\t"+"  \t文件类型\t"+"  \t文件大小\t"+" \t文件内容"+"  \t\t修改时间");
                                    System.out.println(" \t"+cdfcb.name+"   \t\t文件\t"+" \t\t"+cdfcb.size+"  \t\t  "+cdfcb.context+"  \t\t"+cdfcb.time);
                                }
                            }else{
                                System.out.println("文件不存在");
                            }
                        }
                    }
                    break;
                case "change":

                    break;
                case "stop":
                    flag = false;
                    break;
                default:
                    System.out.println("出错了");
                    break;
            }
        }
    }


static void showFat(){//显示FAT表
        for(FATNode fn : fattable){
            System.out.println(fn.nextnode +" | "+fn.otherinfo);
        }
}


static String tt = "";
static void showTree(FCB fcb){//显示树形结构
    System.out.println(tt+fcb.name);
    tt += "\t";
    if (fcb.type == 2){
        for (int i=0; i<fcb.childlist.size(); ++i){
            showTree(fcb.childlist.get(i));
            tt = tt.substring(0,tt.lastIndexOf("\t"));
        }
    }
}

    public static void main(String[] args) {
        Init();
        show();
    }


}

