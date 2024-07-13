package org.battleplugins.arena.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Version object: Capable of asking the important questions:. <br/><br/>
 * 
 * Is the version that's currently installed on the server compatible/supported with a specified version ? <br/><br/>
 * 
 * isCompatible(): Is the installed version greater than or equal to the minimum required version ? <br/><br/>
 * 
 * isSupported(): Is the installed version less than or equal to the maximum required version ? <br/><br/>
 * 
 * @author Europia79, BigTeddy98, Tux2, DSH105, Alkarinv
 */
public class Version implements Comparable<Version> {
    private static final Version SERVER_VERSION = new Version(Bukkit.getVersion());

    private final String version;
    private final BooleanSupplier tester;

    private String separator = "[_.-]";
    
    private Version(String version) {
        this.version = version;
        this.tester = () -> Bukkit.getPluginManager().isPluginEnabled("BattleArena");
    }

    private Version(Plugin plugin) {
        this.version = plugin.getPluginMeta().getVersion();
        this.tester = () -> Bukkit.getPluginManager().isPluginEnabled(plugin.getName());
    }

    /**
     * Checks if the plugin is enabled.
     * <p>
     * This method can be used to run any custom test that you previously 
     * passed to the constructor, then calls tester.test().
     * @return True is the plugin is enabled, or false if it is disabled.
     */
    public boolean isEnabled() {
        return this.tester.getAsBoolean();
    }
    
    /**
     * Alias for isGreaterThanOrEqualTo().
     * 
     * @param minVersion - The absolute minimum version that's required to achieve compatibility.
     * @return Return true, if the currently running/installed version is greater than or equal to minVersion.
     */
    public boolean isCompatible(Version minVersion) {
        return this.isCompatible(minVersion.toString());
    }
    
    /**
     * Alias for isGreaterThanOrEqualTo().
     * 
     * @param minVersion - The absolute minimum version that's required to achieve compatibility.
     * @return Return true, if the currently running/installed version is greater than or equal to minVersion.
     */
    public boolean isCompatible(String minVersion) {
        return this.isGreaterThanOrEqualTo(minVersion);
    }
    
    /**
     * Alias for isLessThanOrEqualTo().
     * 
     * @param maxVersion - The absolute maximum version that's supported.
     * @return Return true, if the currently running/installed version is less than or equal to maxVersion.
     */
    public boolean isSupported(Version maxVersion) {
        return this.isSupported(maxVersion.toString());
    }
    
    /**
     * Alias for isLessThanOrEqualTo().
     * 
     * @param maxVersion - The absolute maximum version that's supported.
     * @return Return true, if the currently running/installed version is less than or equal to maxVersion.
     */
    public boolean isSupported(String maxVersion) {
        return this.isLessThanOrEqualTo(maxVersion);
    }
    
    /**
     * Unlike isCompatible(), this method returns false if the versions are equal.
     * 
     * @param whichVersion - The version to compare against.
     * @return Return true, if the currently running/installed version is greater than whichVersion.
     */
    public boolean isGreaterThan(Version whichVersion) {
        return this.isGreaterThan(whichVersion.toString());
    }
    
    /**
     * Unlike isCompatible(), this method returns false if the versions are equal.
     * 
     * @param whichVersion - The version to compare against.
     * @return Return true, if the currently running/installed version is greater than whichVersion.
     */
    public boolean isGreaterThan(String whichVersion) {
        if (!this.isEnabled()) {
            return false;
        }
        int x = this.compareTo(whichVersion);
        return x > 0;
    }
    
    /**
     * Alias for isCompatible().
     * 
     * @param minVersion - The absolute minimum version that's required to achieve compatibility.
     * @return Return true, if this version object is greater than or equal to the parameter, minVersion.
     */
    public boolean isGreaterThanOrEqualTo(Version minVersion) {
        return this.isGreaterThanOrEqualTo(minVersion.toString());
    }
    
    /**
     * Alias for isCompatible().
     * 
     * @param minVersion - The absolute minimum version that's required to achieve compatibility.
     * @return Return true, if this version object is greater than or equal to the parameter, minVersion.
     */
    public boolean isGreaterThanOrEqualTo(String minVersion) {
        if (!this.isEnabled()) {
            return false;
        }
        int x = this.compareTo(new Version(minVersion));
        return x >= 0;
    }
    
    /**
     * Unlike isSupported(), this method returns false if the versions are equal.
     * 
     * @param whichVersion - The version to compare against.
     * @return Return true, if the currently running/installed version is less than whichVersion.
     */
    public boolean isLessThan(Version whichVersion) {
        return this.isLessThan(whichVersion.toString());
    }
    
    /**
     * Unlike isSupported(), this method returns false if the versions are equal.
     * 
     * @param whichVersion - The version to compare against.
     * @return Return true, if the currently running/installed version is less than whichVersion.
     */
    public boolean isLessThan(String whichVersion) {
        if (!this.isEnabled()) {
            return false;
        }
        int x = this.compareTo(whichVersion);
        return x < 0;
    }
    
    /**
     * Alias for isSupported().
     * 
     * @param maxVersion - The absolute maximum version that's supported.
     * @return Return true, if this version object is less than or equal to the parameter, maxVersion.
     */
    public boolean isLessThanOrEqualTo(Version maxVersion) {
        return this.isLessThanOrEqualTo(maxVersion.toString());
    }
    
    /**
     * Alias for isSupported().
     * 
     * @param maxVersion - The absolute maximum version that's supported.
     * @return Return true, if this version object is less than or equal to the parameter, maxVersion.
     */
    public boolean isLessThanOrEqualTo(String maxVersion) {
        if (!this.isEnabled()) {
            return false;
        }
        int x = this.compareTo(new Version(maxVersion));
        return x <= 0;
    }
    
    /**
     * Returns Negative, Zero, or Positive if this version is less than, equal to, or greater than the parameter.
     * <p>
     * Unlike isCompatible() & isSupported(): THIS METHOD DOES NOT CHECK IF THE PLUGIN IS ENABLED.
     * 
     * @param whichVersion - The version to compare against.
     * @return Negative, Zero, or Positive as this object is less than, equal to, or greater than the parameter.
     */
    @Override
    public int compareTo(Version whichVersion) {
        return this.compareTo(whichVersion.toString());
    }
    
    /**
     * Returns Negative, Zero, or Positive if this version is less than, equal to, or greater than the parameter.
     * <p>
     * Unlike isCompatible() and isSupported(): THIS METHOD DOES NOT CHECK IF THE PLUGIN IS ENABLED.
     * 
     * @param whichVersion - The version to compare against.
     * @return Negative, Zero, or Positive as this object is less than, equal to, or greater than the parameter.
     */
    public int compareTo(String whichVersion) {
        int[] currentVersion = this.parseVersion(this.version);
        int[] otherVersion = this.parseVersion(whichVersion);
        int length = Math.max(currentVersion.length, otherVersion.length);
        for (int index = 0; index < length; index = index + 1) {
            int self = (index < currentVersion.length) ? currentVersion[index] : 0;
            int other = (index < otherVersion.length) ? otherVersion[index] : 0;
            
            if (self != other) {
                return self - other;
            }
        }
        return 0;
    }
    
    /**
     * A typical version of 1.2.3.4-b567 will be broken down into an array. <br/><br/>
     * 
     * [1] [2] [3] [4] [567]
     */
    private int[] parseVersion(String versionParam) {
        versionParam = (versionParam == null) ? "" : versionParam;
        String[] stringArray = versionParam.split(separator);
        int[] temp = new int[stringArray.length];
        for (int index = 0; index <= (stringArray.length - 1); index = index + 1) {
            String t = stringArray[index].replaceAll("\\D", "");
            try {
                temp[index] = Integer.parseInt(t);
            } catch(NumberFormatException ex) {
                temp[index] = 0;
            }
        }
        return temp;
    }
    
    /**
     * The default regex separator is "[_.-]".
     * <p>
     * Which separates by underscores, periods, & dashes.
     */
    public Version setSeparator(String regex) {
        this.separator = regex;
        return this;
    }
    
    /**
     * Searches for possible Development builds.
     */
    public boolean search(String regex) {
        if (this.version == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.version);
        return matcher.find();
    }
    
    /**
     * Used to get a Sub-Version (or Development build). <br/><br/>
     * @param regex - The regular expression to search for.
     * @return A completely new Version object.
     */
    public Version getSubVersion(String regex) {
        if (this.version == null) {
            return this;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(this.version);
        String dev = this.version;
        if (matcher.find()) {
            dev = matcher.group();
        }
        return new Version(dev);
    }
    
    /**
     * This is the exact, un-altered String that was passed to the constructor.
     * @return The String representation, or an empty String if null.
     */
    @Override
    public String toString() {
        return (this.version == null) ? "" : this.version;
    }

    /**
     * Creates a new Version object from the given
     * version string.
     * 
     * @param version The version string
     * @return The new Version object
     */
    public static Version of(String version) {
        return new Version(version);
    }
    
    /**
     * Creates a new Version object from the given
     * plugin.
     * 
     * @param plugin The plugin
     * @return The new Version object
     */
    public static Version of(Plugin plugin) {
        return new Version(plugin);
    }
    
    /**
     * Creates a new Version object from the server's
     * version.
     * 
     * @return The new Version object
     */
    public static Version getServerVersion() {
        return SERVER_VERSION;
    }
}