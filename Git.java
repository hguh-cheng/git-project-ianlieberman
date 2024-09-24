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
    public static boolean doCompress;
    //private static MessageDigest md;
    public Git (boolean doCompress) {
        this.doCompress = doCompress;
    }

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


    public static void newBlob(File f) throws IOException, NoSuchAlgorithmException { //Hashes OG file data to make name, save new file to objects folder, zip and copy data into new file, and add blob name and OG File name into index file

        File blob = new File ("./git/objects/" + hashFile(f));

        blob.createNewFile();
        if(doCompress) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(blob));

            zos.putNextEntry(new ZipEntry(f.getPath()));

            zos.write(Files.readAllBytes(f.toPath()));
            zos.closeEntry();
            zos.close();

            Files.writeString(Path.of("./git/index"), blob.getName() + " " + f.getName() + '\n');
        }
        else { // If they dont want to compress, reads bytes from OG File and converts to charsequence/string for Files.writeString

            Files.writeString(Path.of(blob.getPath()), new String(Files.readAllBytes(Path.of(f.getPath())), StandardCharsets.UTF_8));

            Files.writeString(Path.of("./git/index"), blob.getName() + " " + f.getName() + '\n');
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