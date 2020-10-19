package me.cirq.obfdetector;


import me.cirq.obfdetector.analyzer.ManifestAnalyzer;
import picocli.CommandLine;
import picocli.CommandLine.PicocliException;

import java.nio.file.Path;
import java.util.List;

import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;


public class Config {

    @Parameters(index="0", description="path to apk file")
    public Path apkFile;

    @Option(names={"-h", "--help"}, usageHelp=true, description="display a help message")
    private boolean helpRequested = false;

    @Option(names={"-s", "--sdk"}, required=true, description="path to android sdk jar")
    private Path androidJarDir;

    @Option(names={"--obf-th"}, defaultValue="0.1", description="threshold to judge obfuscation")
    public double obfuscationThreshold;

    public Path versionSdkFile = null;
    public List<String> excludedPkgs = null;



    public void init(String... args) {
        try {
            CommandLine cmd = new CommandLine(this);
            cmd.parseArgs(args);
            if(helpRequested){
                cmd.usage(cmd.getOut());
                System.exit(cmd.getCommandSpec().exitCodeOnUsageHelp());
            }

            int apkTargetVersion = ManifestAnalyzer.INSTANCE.getTargetSdkVersion();
            versionSdkFile = androidJarDir.resolve("android-"+apkTargetVersion).resolve("android.jar");
        } catch (PicocliException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }


    private Config(){}

    private static Config singleton = null;

    public static Config get() {
        if(singleton == null)
            singleton = new Config();
        return singleton;
    }

}
