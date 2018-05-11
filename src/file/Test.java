package file;

import java.io.File;
import java.io.IOException;

public class Test {
    static File curFile = null;
    static void createCatalog(String path){//创建目录
         File cfile = new File(path);
         if (cfile.exists()){
             System.out.println("文件已经存在");
         }else{
             cfile.mkdir();
         }
    }
    static void createFile(String path) throws IOException {//创建文件
        File cfile = new File(path);
        if (cfile.exists()){

        }else{
            cfile.createNewFile();
        }
    }

    static void deleteFile(File file){//删除文件
        if (file.exists()){
            file.delete();
        }else{
            System.out.println("不存在");
        }
    }

    static void show1(File file){//显示当前目录下的文件和目录
        System.out.println("当前目录下的文件:");
        String[] strs = file.list();
        for(String str : strs){
            System.out.println(str);
        }
    }

    static void CD(String path){//定位到指定路径
        File cdfile = new File(path);
        if(cdfile.exists()){
            curFile = cdfile;
        }
    }

    public static void main(String[] args) {
        boolean flag = true;
    }







}

