package xens.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static void main(String[] args) throws Exception {
        List<String> fileNames = new ArrayList<String>();
        FileUtil.findFileList(new File("C:\\Users\\96212\\Desktop\\1"),fileNames );
        System.out.println();
        for (String value :  fileNames) {
            System.out.println("file:"+value);
        }

    }


    /**
     * 读取目录下的所有文件
     *
     * @param dir
     *            目录
     * @param fileNames
     *            保存文件名的集合
     * @return
     */
    public static void findFileList(File dir, List<String> fileNames) {
        if (!dir.exists() || !dir.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = dir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < files.length; i++) {// 循环，添加文件名或回调自身
            File file = new File(dir, files[i]);
            if (file.isFile()) {// 如果文件
                fileNames.add(dir + "\\" + file.getName());// 添加文件全路径名
            } else {// 如果是目录
                findFileList(file, fileNames);// 回调自身继续查询
            }
        }
    }
}