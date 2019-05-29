package com.siang.wei.mybookmark.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.*;

public class SharedFileMethod {

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws IOException {
        String ret;
        File file = new File(filePath);
        FileInputStream fin = new FileInputStream(file);
        try {
            ret = convertStreamToString(fin);
        } finally {
            if(fin != null) {
                fin.close();
            }
        }
        //Make sure you close all streams.
        return ret;
    }

    public static boolean isExist(String patch) {

        File file = new File(patch);
        if(file != null) {
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean isFolderExist(String patch, boolean isCreate) {

        File file = new File(patch);
        if(file != null) {
            if (file.exists()) {
                return true;
            } else {
                if(isCreate) {
                    return file.mkdirs();
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public static void copyDirectory(String source, String target) throws IOException {
        File sourceFolder = new File(source);
        File targetFolder = new File(target);

        copyDirectory(sourceFolder, targetFolder);
    }

    public static void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(
                        targetLocation, children[i]));
            }
        } else {

            copyFile(sourceLocation, targetLocation);
        }
    }

    /**
     * @param sourceLocation
     * @param targetLocation
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void copyFile(File sourceLocation, File targetLocation)
            throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);

        copyFile(in, out);

        in.close();
        out.close();
    }

    // Copy the bits from instream to outstream
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static void saveFile(String content, String path) throws IOException {
        FileOutputStream fop = null;
        File file = new File(path);

        if(!file.getParentFile().exists()) {
            file.mkdirs();
        }

        if (file.exists()) { // 如果檔案不存在，建立檔案
            boolean isDelete = file.delete();
            if(isDelete)
                file.createNewFile();
        } else {
            file.createNewFile();
        }


        fop = new FileOutputStream(file);
        byte[] contentInBytes = content.getBytes();// 取的字串內容bytes

        fop.write(contentInBytes); //輸出

        fop.flush();
        fop.close();
    }

    public static String copyDownloadFile(FileDescriptor fileDescriptor, String outputFolder, String fileName) {
//        File sourceFile = new File(input);
//        if(!sourceFile.exists())
//            return null;

        if(SharedFileMethod.isFolderExist(outputFolder, true)) {
            File desFile = new File(combinePath(outputFolder, fileName));
            if (desFile.exists())
                desFile.delete();
            try {
                InputStream in = new FileInputStream(fileDescriptor);
                OutputStream out = new FileOutputStream(desFile);
                copyFile(in, out);
//                sourceFile.delete();
                return desFile.getAbsolutePath();
            } catch (IOException e) {
                return null;
            }
        } else {
            return null;
        }
    }


    public static boolean deleteDirectory(String pathString) {
       return deleteDirectory(new File(pathString));
    }

    public static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }


    public static String[] getAssetsFiles(Context context, String pathName) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(pathName);
        } catch (IOException e) {
            Log.d("CopyAssets", "Failed to get asset file list.", e);
        }

        return files;
    }

    public static boolean copyAssets(Context context, String pathName, String outputPath) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(pathName);
        } catch (IOException e) {
            Log.d("CopyAssets", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(pathName + File.separator + filename);

                boolean isFolderExist = isFolderExist(outputPath, true);
                if(isFolderExist) {
                    File outFile = new File(outputPath, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } else {
                    Log.d("CopyAssets", "Failed to create folder: " + outputPath);
                    return false;
                }
            } catch(IOException e) {
                // maybe is Floder
//                Log.d("tag", "Failed to copy asset file: " + filename, e);
                boolean isCopyAssetFolder = copyAssets(context, pathName + File.separator + filename, outputPath + File.separator + filename);
                if(isCopyAssetFolder) {
                    Log.d("CopyAssets", "Passed to copy asset file: " + pathName + File.separator + filename);
                } else {
                    return false;
                }
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
        return true;
    }

    /**
     * Remove the file extension from a filename, that may include a path.
     *
     * e.g. /path/to/myfile.jpg -> /path/to/myfile
     * e.g. myfile.jpg -> myfile
     */
    public static String getFileNameWithoutExtentsion(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }

    /**
     * Return the file extension from a filename, including the "."
     *
     * e.g. /path/to/myfile.jpg -> .jpg
     * e.g. myfile.jpg -> .jpg
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = indexOfExtension(filename);

        if (index == -1) {
            return filename;
        } else {
            return filename.substring(index);
        }
    }

    private static final char EXTENSION_SEPARATOR = '.';
    private static final char DIRECTORY_SEPARATOR = '/';

    private static int indexOfExtension(String filename) {

        if (filename == null) {
            return -1;
        }

        // Check that no directory separator appears after the
        // EXTENSION_SEPARATOR
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);

        int lastDirSeparator = filename.lastIndexOf(DIRECTORY_SEPARATOR);

        if (lastDirSeparator > extensionPos) {
            return -1;
        }

        return extensionPos;
    }

    // Joins two path components, adding a separator only if necessary.
    public static String combinePath(String parent, String add) {
        int prefixLength = parent.length();
        boolean haveSlash = (prefixLength > 0 && parent.charAt(prefixLength - 1) == File.separatorChar);
        if (!haveSlash) {
            haveSlash = (add.length() > 0 && add.charAt(0) == File.separatorChar);
        }
        return haveSlash ? (parent + add) : (parent + File.separatorChar + add);
    }


}
