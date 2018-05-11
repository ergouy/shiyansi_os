import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CeShi {
    public static void main(String[] args) throws IOException {
        //创建文件夹
        File file = new File("D://os");
        if(!(file.exists())){
            file.mkdir();
        }
        //创建文件
        File text = new File(file,"test.txt");
        if(text.exists()){

        }else{
            text.createNewFile();
        }
        text.delete();
        file.delete();
    }
}
