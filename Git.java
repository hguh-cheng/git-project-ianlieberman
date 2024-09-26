import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Git
{
    public static boolean doCompress = false;
    //private static MessageDigest md;
    // public Git (boolean doCompress) {
    //     this.doCompress = doCompress;
    // }

    public static void initRepo() throws IOException {

        if(Files.exists(Path.of("./git/objects")) && Files.exists(Path.of("./git/index"))){

            System.out.println("Git Repository already exists");
        }
        else {
            if(!Files.exists(Path.of("./git"))) {
                File newDir = new File("./git");
                newDir.mkdir();
            }

            if(!Files.exists(Path.of("./git/objects"))) {
                File newDir = new File("./git/objects");
                newDir.mkdir();
            }

            if(!Files.exists(Path.of("./git/index"))) {
                Files.createFile(Path.of("./git/index"));

            }
        }
    }  // Checks if a git repo exists, if not, makes essential files, redundantly checking if they already exist on the way


    public static String hashFile(File f) throws IOException, NoSuchAlgorithmException { //Checks if file exists, if so, it reads the contents into a byte array, converts that into SHA then returns hex
        MessageDigest md = MessageDigest.getInstance("SHA-1");


        if(!Files.exists(f.toPath())){
            throw new FileNotFoundException();
        }

        byte[] fContents = Files.readAllBytes(Path.of(f.getPath()));

        String hex = "";
        for (byte i : md.digest(fContents)) {
                hex += String.format("%02X", i);
        }



        return hex;
    } // Reads out files contents into byte array then hashes it with MessageDigest with SHA-1


    public static void newBlob(File f, boolean tree) throws IOException, NoSuchAlgorithmException { //Hashes OG file data to make name, save new file to objects folder, zip and copy data into new file, and add blob name and OG File name into index file

        
        File blob = new File ("./git/objects/" + hashFile(f));
        blob.createNewFile();
        File index = new File ("./git/index");
        BufferedWriter writer = Files.newBufferedWriter(index.toPath(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        if(tree)
            {
                writer.append("tree ");
            }
            else{
                writer.append("blob ");
            }
        if(doCompress) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(blob));

            zos.putNextEntry(new ZipEntry(f.getPath()));

            zos.write(Files.readAllBytes(f.toPath()));
            zos.closeEntry();
            zos.close();

            Files.writeString(Path.of("./git/index"), blob.getName() + " " + f.getName() + '\n');
        }
         // If they dont want to compress, reads bytes from OG File and converts to charsequence/string for Files.writeString
        else{
            

        
            Files.write(Path.of(blob.getPath()), new String(Files.readAllBytes(Path.of(f.getPath())), StandardCharsets.UTF_8).getBytes(), StandardOpenOption.APPEND);
            String hash = hashFile(f);
            writer.append(blob.getName() + " " + f.getPath() + '\n');

            writer.close();
        }
        
    }
    public static String newDirectoryBlob(Path p) throws NoSuchAlgorithmException, IOException
        {
            if (!p.toFile().exists())
            {
                throw new FileNotFoundException("Directory doesn't currently exist");
            }
            File f = p.toFile();
            if (!f.isDirectory())
            {
                newBlob(f, false);
                return hashFile(f);
            }
            else{
                File[] files = f.listFiles();
                for (int i = 0; i < files.length; i++)
                {
                    newDirectoryBlob(files[i].toPath());
                }
                
                File tree = createTree(f);
                newBlob(tree, true);
                return hashFile(tree);
            }
        }

        public static File createTree(File f) throws IOException, NoSuchAlgorithmException
        {
            if (!f.isDirectory()) {
                System.out.println("not a directory, can't make a tree");
                return null;
            } else {
                File[] files = f.listFiles();
                File fileName = new File ("./git/tree" + f.getName());
                if (!fileName.exists())
                {
                    Files.createFile(fileName.toPath());
                }
                BufferedWriter writer = Files.newBufferedWriter(fileName.toPath());
                for (int i = 0; i < files.length; i++)
                {
                    if (files[i].isDirectory())
                    {
                        writer.append("tree " + hashFile(createTree(files[i])) + " " + files[i].getPath() + "\n"); // this is the line that doesn't work, you can'tcreate a new Directory blob.
                    }
                    else{
                        writer.append("blob " + hashFile(files[i]) + " " + files[i].getPath() + "\n");
                    }
                }
                writer.close();
                return fileName;
            }
        }
    /*References
     * http://www.sha1-online.com/sha1-java/
     * https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
     * https://stackoverflow.com/questions/858980/file-to-byte-in-java
     * https://stackoverflow.com/questions/3849692/whole-text-file-to-a-string-in-java
     * https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/Files.html#readString(java.nio.file.Path)
     * https://docs.oracle.com/javase/8/docs/api/java/io/File.html
     * https://stackoverflow.com/questions/1536054/how-to-convert-byte-array-to-string-and-vice-versa
     * https://www.geeksforgeeks.org/java-program-to-convert-byte-array-to-hex-string/
     * https://www.codejava.net/java-se/file-io/how-to-compress-files-in-zip-format-in-java
     */
}