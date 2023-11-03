package dev.alonebown.bteuinstaller;

import com.google.gson.*;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
public class InstallTask extends SwingWorker<Void, Integer> {
    String modpackDownloadURL;
    String fabricDownloadURL;
    String cmdKeybindURL;
    String replayModURL;
    String fabricLoaderVersion;
    String bteGermanyModpackVersion;
    InstallUtil installUtil;
    JProgressBar progressBar;
    JLabel progessLabel;
    JDialog progressDialog;
    String fileSeparator;
    String modpackVersion;
    String panoramicaModURL;
    public InstallTask(InstallUtil installUtil, JProgressBar progressBar, JLabel progessLabel, JDialog progressDialog, String modpackVersion) {
        this.installUtil = installUtil;
        this.progressBar = progressBar;
        this.progessLabel = progessLabel;
        this.progressDialog = progressDialog;
        this.modpackVersion = modpackVersion;
    }

    @Override
    protected Void doInBackground() {
        switch (modpackVersion) {
                case "1.20.2":
                    modpackDownloadURL = "https://cdn.discordapp.com/attachments/561102539265802243/1169292842812837899/modpack.zip";
                    fabricDownloadURL = "https://cdn.discordapp.com/attachments/561102539265802243/1168964809144930304/fabric.zip";
                    cmdKeybindURL = "https://cdn.modrinth.com/data/h3r1moh7/versions/snLr0hHP/cmdkeybind-1.6.3-1.20.jar";
                    replayModURL = "https://cdn.modrinth.com/data/Nv2fQJo5/versions/akFkhrL8/replaymod-1.20.1-2.6.13.jar";
                    panoramicaModURL = "https://cdn.discordapp.com/attachments/561102539265802243/1169291741376041011/panoramica__fabric_1.2.1_MC_1.20.2.jar";
                    fabricLoaderVersion = "fabric-loader-0.14.22-1.20.2";
                    bteGermanyModpackVersion = "BTE Ukraine v1.1";
                    break;
                default:
                    throw new RuntimeException("Версія модпаку не підтримується");
            }
            fileSeparator = FileSystems.getDefault().getSeparator();
            File installationPath = getMinecraftDir("bteukraine").toFile();
            File minecraftPath = getMinecraftDir("minecraft").toFile();
            if (!installationPath.exists()) {
                installationPath.mkdir();
            }

        try {
            // MODPACK
            deleteOldFiles(installationPath);
            downloadModpack(modpackDownloadURL, installationPath);
            unzipModpack(installationPath);
            //unzipFile(Paths.get(new File(installationPath.getAbsolutePath() + fileSeparator + "modpack.zip").getPath()));
            File modpackArchive = new File(installationPath.getAbsolutePath() + fileSeparator + "modpack.zip");
            modpackArchive.delete();
            downloadOptionalMods(installationPath + fileSeparator + "mods" + fileSeparator);

            // FABRIC
            downloadFabric(minecraftPath.getAbsolutePath().toString(), new URL(fabricDownloadURL));
            //unzipFile(Paths.get(new File(minecraftPath.getAbsolutePath() + fileSeparator + "versions"+ fileSeparator + "fabric.zip").getPath()));
            unzipFabric(minecraftPath);
            File fabricArchive = new File(minecraftPath.getAbsolutePath() + fileSeparator + "versions"+ fileSeparator + "version.zip");
            fabricArchive.delete();
            editLauncherProfiles(minecraftPath, installationPath);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void done() {
        InstallUtil.playSound("notification.wav");
        progressDialog.setTitle("Встановлення/оновлення завершено");
        progessLabel.setText("Встановлення/оновлення завершено");
        progressBar.setValue(100);
    }

    private static Path getMinecraftDir(String mcFolderNanme) {
        String home = System.getProperty("user.home", ".");
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (os.contains("win") && System.getenv("APPDATA") != null) {
            return Paths.get(System.getenv("APPDATA"), "."+mcFolderNanme);
        } else if (os.contains("mac")) {
            return Paths.get(home, "Library", "Application Support", mcFolderNanme);
        }
        return Paths.get(home, "."+mcFolderNanme);
    }

    private boolean deleteOldFiles(File installationPath) {
        progessLabel.setText("Видаляємо старі файли...");
        ArrayList<File> files = new ArrayList<>();
        files.add(new File(installationPath.getAbsolutePath() + fileSeparator + "config"));
        files.add(new File(installationPath.getAbsolutePath() + fileSeparator + "fancymenu_data"));
        files.add(new File(installationPath.getAbsolutePath() + fileSeparator + "fancymenu_setups"));
        files.add(new File(installationPath.getAbsolutePath() + fileSeparator + "mods"));
        files.add(new File(installationPath.getAbsolutePath() + fileSeparator + "resources"));
        for (File file : files) {
            if (file.isDirectory()) {
                try {
                    deleteDirectoryRecursion(file.toPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private boolean downloadModpack(String modpackDownloadURL, File installationPath) throws IOException {
        progessLabel.setText("Завантажуємо модпак...");
        URL url = new URL(modpackDownloadURL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        long fileSize = httpURLConnection.getContentLength();
        long downloadedFileSize = 0;
        BufferedInputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(installationPath.getAbsolutePath() + fileSeparator + "modpack.zip"), 1024);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            downloadedFileSize++;
            progressBar.setValue((int) ((((double) downloadedFileSize) / ((double) fileSize)) * 100000d));
        }
        in.close();
        out.close();
        return true;
    }

    private boolean unzipModpack(File installationPath) throws IOException {
        System.out.println("Розархівовуємо модпак...");
        ZipInputStream zis = new ZipInputStream(new FileInputStream(installationPath.getAbsolutePath() + fileSeparator + "modpack.zip"));
        ZipEntry zipEntry = zis.getNextEntry();
        long fileSize = zipEntry.getCompressedSize();

        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            String filePath = installationPath + fileSeparator + fileName;
            System.out.println("Extracting file: " + filePath);
            File file = new File(filePath);

            if (!file.exists()) {
                if (zipEntry.isDirectory()) {
                    file.mkdir();
                    System.out.println("Created directory: " + filePath);
                } else {
                    long zipFileSize = 0;

                    FileOutputStream fos = new FileOutputStream(filePath);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        zipFileSize++;
                        progressBar.setValue((int) ((((double) zipFileSize) / ((double) fileSize)) * 100000d));
                    }
                    fos.close();
                    System.out.println("Extracted file: " + filePath);
                }
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        System.out.println("Встановлення модпаку завершено.");
        return true;
/*

        System.out.println("Entpacke Modpack...");

        TFile archive = new TFile(installationPath.getAbsolutePath() + fileSeparator + "modpack.zip");
        TFile directory = new TFile(installationPath);
        TFile.cp_rp(archive, directory, TArchiveDetector.NULL, TArchiveDetector.NULL);

        System.out.println("Modpack extraction complete.");
        return true;

         */
    }
    /*
    private static void unzipFile(Path filePathToUnzip) {

        Path targetDir = filePathToUnzip.getParent();

        //Open the file
        try (ZipFile zip = new ZipFile(filePathToUnzip.toFile())) {

            FileSystem fileSystem = FileSystems.getDefault();
            Enumeration<? extends ZipEntry> entries = zip.entries();

            //We will unzip files in this folder
            if (!targetDir.toFile().isDirectory()
                    && !targetDir.toFile().mkdirs()) {
                throw new IOException("failed to create directory " + targetDir);
            }

            //Iterate over entries
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File f = new File(targetDir.resolve(Paths.get(entry.getName())).toString());

                //If directory then create a new directory in uncompressed folder
                if (entry.isDirectory()) {
                    if (!f.isDirectory() && !f.mkdirs()) {
                        throw new IOException("failed to create directory " + f);
                    }
                }

                //Else create the file
                else {
                    File parent = f.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("failed to create directory " + parent);
                    }

                    try(InputStream in = zip.getInputStream(entry)) {
                        Files.copy(in, f.toPath());
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    private boolean downloadOptionalMods(String modsFolder) throws IOException {
        if (installUtil.isOptionalModEnabled(OptionalMod.COMMAND_MACROS)) {
            downloadMod(modsFolder, new URL(cmdKeybindURL));
        }
        if (installUtil.isOptionalModEnabled(OptionalMod.REPLAY_MOD)) {
            downloadMod(modsFolder, new URL(replayModURL));
        }
        if (installUtil.isOptionalModEnabled(OptionalMod.PANORAMICA)) {
            downloadMod(modsFolder, new URL(panoramicaModURL));
        }
        return true;
    }

    private boolean downloadMod(String modsFolder, URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        long fileSize = httpURLConnection.getContentLength();
        long downloadedFileSize = 0;
        String modName = new File(url.getPath().toString()).getName();

        progessLabel.setText("Завантажуємо " + modName + "...");
        BufferedInputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(modsFolder + fileSeparator + modName), 1024);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            downloadedFileSize++;
            progressBar.setValue((int) ((((double) downloadedFileSize) / ((double) fileSize)) * 100000d));
        }
        in.close();
        out.close();
        return true;
    }

    private boolean downloadFabric(String minecraftFolder,URL url) throws IOException{
        progessLabel.setText("Завантажуємо Fabric...");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");
        long fileSize = httpURLConnection.getContentLength();
        long downloadedFileSize = 0;
        BufferedInputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(minecraftFolder + fileSeparator + "versions"+ fileSeparator + "version.zip"), 1024);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
            downloadedFileSize++;
            progressBar.setValue((int) ((((double) downloadedFileSize) / ((double) fileSize)) * 100000d));
        }
        in.close();
        out.close();
        return true;
    }

    private boolean unzipFabric(File minecraftFolder) throws IOException {
        progessLabel.setText("Розархівовуємо Fabric...");
        ZipInputStream zis = new ZipInputStream(new FileInputStream(minecraftFolder + fileSeparator + "versions" + fileSeparator + "version.zip"));
        ZipEntry zipEntry = zis.getNextEntry();
        long fileSize = zipEntry.getCompressedSize();

        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            String filePath = minecraftFolder + fileSeparator + "versions" +  fileSeparator + fileName;

            if(!new File(filePath).exists()) {
                if (zipEntry.isDirectory()) {
                    File dir = new File(filePath);
                    dir.mkdir();
                } else {
                    long zipFileSize = 0;

                    FileOutputStream fos = new FileOutputStream(filePath);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        zipFileSize++;
                        progressBar.setValue((int) ((((double) zipFileSize) / ((double) fileSize)) * 100000d));
                    }
                    fos.close();
                }
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        return true;
    }

    private boolean editLauncherProfiles(File minecraftFolder, File installationPath){
        progessLabel.setText("Додаємо профіль у Лаунчері...");
        try {
            FileReader reader = new FileReader(minecraftFolder + fileSeparator + "launcher_profiles.json");
            Gson gson = new Gson();
            JsonParser parser = new JsonParser();

            JsonObject newEntry = new JsonObject();

            newEntry.addProperty("gameDir", installationPath.getAbsolutePath().toString());
            newEntry.addProperty("icon", "data:image/png;base64,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");
            newEntry.addProperty("lastVersionId", fabricLoaderVersion);
            newEntry.addProperty("name", bteGermanyModpackVersion);
            newEntry.addProperty("type", "custom");

            JsonElement element = parser.parse(reader);

            element.getAsJsonObject().get("profiles").getAsJsonObject().add("BTE Ukraine", newEntry);

            FileWriter writer = new FileWriter(minecraftFolder + fileSeparator + "launcher_profiles.json");
            gson.toJson(element, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private void deleteDirectoryRecursion(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);
    }
}
