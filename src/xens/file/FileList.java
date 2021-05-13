package xens.file;

import java.io.File;
import java.util.List;

public class FileList {
    public static void findFileList(File fileDir, List<String> fileNames) {
        if (!fileDir.exists() || !fileDir.isDirectory()) {// 判断是否存在目录
            return;
        }
        String[] files = fileDir.list();// 读取目录下的所有目录文件信息
        for (int i = 0; i < files.length; i++) {// 循环，添加文件名或回调自身
            File file = new File(fileDir, files[i]);
            if (file.isFile()) {// 如果文件
                fileNames.add(fileDir + "\\" + file.getName());// 添加文件全路径名
            } else {// 如果是目录
                findFileList(file, fileNames);// 回调自身继续查询
            }
        }
    }

}
