import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class gitTester {
    private static MessageDigest md;
    public static void main(String[]args) throws IOException, NoSuchAlgorithmException {
         md = MessageDigest.getInstance("SHA-1");

        initRepoTester();
        DirectoryTester();
         //System.out.println(newBlobTester());



        //clearDir(new File("./git"));
        //Files.deleteIfExists(Paths.get("./git/"));

    }
    public static void DirectoryTester() throws IOException, NoSuchAlgorithmException
    {
        File OutsideFolder = new File("./folder1");
        File InsideFolder = new File("./folder1/folder2");
        File InsideFile = new File("./folder1/file1.txt");
        File InsideInsideFile = new File("./folder1/folder2/file2.txt");
        File InsideInsideFolder = new File("./folder1/folder2/folder3"); //doesn't work currently

        if (!OutsideFolder.exists())
        {
            OutsideFolder.mkdir();
        }
        if (!InsideFolder.exists())
        {
            InsideFolder.mkdir();

        }
        if (!InsideInsideFolder.exists())
        {
            InsideInsideFolder.mkdir();

        }
        if (!InsideFile.exists())
        {
            InsideFile.createNewFile();
            BufferedWriter writer = Files.newBufferedWriter(InsideFile.toPath());
            writer.write("This is some gibberish \n I really hope this works");
            writer.close();

        }
        if (!InsideInsideFile.exists())
        {
            InsideInsideFile.createNewFile();
            BufferedWriter writer = Files.newBufferedWriter(InsideInsideFile.toPath());
            writer.write("This is some worse gibberish and I really really hope this file works");
            writer.close();
        }
        Git.newDirectoryBlob(OutsideFolder.toPath());

    }
    public static boolean initRepoTester() throws IOException { //Runs initRepo and checks if the repo was initted
        File gitDir = new File("./git");

        Git.initRepo();

        if(Files.exists(Path.of("./git/objects")) && Files.exists(Path.of("./git/index"))) {
            for(File f:gitDir.listFiles()) {
                System.out.println(f.getName());
                //f.delete();
            }
            //gitDir.delete();
            System.out.println("initRepo worked");
            return true;
        }
        System.out.println("initRepo failed");
        return false;
    }

    public static void clearDir(File dir) throws IOException { //Recursively clears a directory, 30% sure this doesn't work
        try {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    clearDir(f);
                } else {
                    f.delete();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("clearDir failed");
        }

    }

    public static boolean newBlobTester() throws IOException, NoSuchAlgorithmException { //Makes 100 files filled randomly and stores array of expected hashed names, creates blobs for all the files, and returns false if any of the files don't match up when compared
        File blobTests = new File("./git/blobTests");
        System.out.println(blobTests.mkdir());

        String[] blobNames = new String[10];

        for(int i = 0; i < blobNames.length; i++) {
            File f = new File("./git/blobTests/" + i);
            f.createNewFile();
            blobNames[i] = fillRandAndSHA(new File("./git/blobTests/" + i));

            Git.newBlob(new File("./git/blobTests/" + i), false);
        }
        File directory = new File ("./git/blobTests/directory");




        for(int i = 0; i < blobNames.length; i++) {
            if(!Files.exists(Path.of("./git/objects/" + blobNames[i]))) {
                return false;
            }
        }

        //clearDir(new File("./git/blobTests"));
        //Files.deleteIfExists(Path.of("./git/blobTests"));

        return true;
    }

    public static String fillRandAndSHA(File f) throws IOException { //Takes file, inputs 500 random digits 1-9, then returns the SHA1 name it should have
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 500; i++) {
            sb.append((int) (Math.random() * 9));
        }

        Files.writeString(Path.of(f.getPath()), sb);


        String hex = "";
        for (byte i : md.digest(sb.toString().getBytes())) {
            hex += String.format("%02X", i);
        }

        return hex;
    }
    /*MAKE TESTER MAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTERMAKE TESTER*/
}
