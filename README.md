# NightscoutLoader

## Why the name 'Nightscout' Loader?

* Because [Nightscout](http://www.nightscout.info/) is the CGM in the Cloud environment that this tool supports.

## What is [Nightscout Loader](https://david50703.wixsite.com/nightscoutloader)?

A desktop application with the following purposes in mind:

* Synchronize data from meter/pump as treatments in Nightscout Care Portal.
* Optonally also now synchronize Diasend data from CGM as entries in Nightscout.
* Quick way of locating and allowing edits to the Notes field in treatment data.
* Analysis of BG Trends
* Analysis of CGM Data
* Easier access to Autotune

### Synchronize data from meter/pump as treatments in Nightscout Care Portal.

![picture](resources/images/MainPage.jpg)

Meter & Pump Data can be regularly loaded from a range of systems:
  * Libre view (csv)
  * Diasend (xls)
  * Medtronic (csv)
  * Tandem t:slim (csv)
  * Cellnovo (csv)

  
A serial stream of BG, Carb and Insulin is intelligently grouped together into a Treatment.  The tool will ignore previously loaded data.  It will also identify contention between manually entered Care Portal entries and data from meter/pump.
  
### Quick way of locating and allowing edits to the Notes field in treatment data.

![picture](resources/images/FindModify.jpg)

Searches can be performed over combinations of different fields, date ranges, treatment types and also if the loaded entry is a duplicate with a manually entered Care Portal entry.

### Analysis of BG Trends

![picture](resources/images/TreatmentTrendAnalysis.jpg)

Analysis can be run over selected date ranges and outputs to Excel for convenient archiving of analytical snapshots.
Analysis also runs in background on start up and provides a condensed summary of top 3 trends

### Analysis of CGM Data

![picture](resources/images/CGMTrendAnalysis.jpg)

Any CGM data available within selected analysis date range is also analyzed.  A heat map shows areas of high frequency profiles.

#### Inference of CGM Date Ranges

![picture](resources/images/AnalysisCGMDates.JPG)

For those without continual sensor use, identifies date ranges when sensor was worn - useful for reviewing past events & data.


### Easier access to Autotune

Nightscout Loader can now help with running and maintaining profiles for Autotune

![picture](resources/images/Autotune_MainPage.JPG)

As well as integrating Autotune output in the Excel analysis results file, an "Autotune Management" window allows easy access to run Autotune once configured.


![picture](resources/images/Autotune_ProfileEditorPage.JPG)

A Profile editor is provided that can edit the remote profile, open profile files on the local machine and also read profile data from a Diasend export.


![picture](resources/images/Autotune_ListBackups.JPG)

Each time the Profile Editor updates the profile on the remote Autotune server, it takes a backup into a separate directory.  The files in the backup directory can be listed (as above) and also downloaded onto the local machine for greater safe keeping.

## Analytic approach used by NightscoutLoader

![picture](resources/images/TrendAnalysis.jpg)

Nightscout Loader helps get real **Insights** from reams of *Data*.

## Is it for me?

- [x] Do you use Nightscout and want to upload Treatment (and or Diasend CGM) data to Nightscout from a supported Meter and or Pump?
    - Then set up for Nightscout use
- [x] Do you want an extra tool to help analyze BGs, Carbs & Insulin?  (Nightscout use is optional)
    -  Then set up either for Nightscout use or non-Nightscout use
- [x] Do you use Nightscout and want an extra tool to visualize repeated patterns of CGM data?
- [x] Do you have Autotune configured on a separate Linux machine and want assistance with using it - particularly if you also use Diasend?
    - Features were initially incorporated to help with managing a virtual Linux server like AWS to run AutoTune.  Since then, [Autotune Web](https://autotuneweb.azurewebsites.net/) is available and NightscoutLoader does not yet integrate

If any of the above are true, then it could help you.

## Installation Guide

### System Requirements

The application is built for desktop or laptop computers with Java.  It requires at least Java version 8 and the installation guide includes a link to check and install if necessary.


It loads the Nightscout Treatment and CGM into memory for comparison with new data being uploaded or for analysis.  The amount of memory your system needs will depend on how much data you pull in.  As a guide, 8 years' worth of treatment data and 20,000 CGM entries takes up 300Mb of RAM.


### Installation Guide for Nightscout use on Windows

1. Check your version of Java at this [link](https://java.com/en/download/help/version_manual.xml)
1. Download the application from the [Github Releases](https://github.com/gh-davidr/NightscoutLoader/releases) page.
    1. Occassionally, an early beta version is made available with fixes on the stable version or access to upcoming features.
2. Copy the **NightScoutLoader.jar** and **NightScoutLoader.bat** files to your desktop (or any other location), ensuring that both files **are in the same folder**.
    1. (Note that the loadLibreview.sh and loadDiasend.sh files are template Linux files for running NightscoutLoader as a batch and so don't need to be copied)
3. Launch the application by double-clicking the **NightScoutLoader.bat** file
    1. The **NightScoutLoader.bat** script checks the jar file is correctly located.  If it is not correctly located, then it will alert and exit.
    1. It will also attempt to create C:\Temp folder for the main application log file.  If this attempt fails then it will alert and exit.
4. Click Tools => Settings to launch the Settings panel
5. Complete the sections in Yellow
6. For example:
    1. if your user is "my_mongo_user"
    2. password is "my_mongo_pass"
    4. database is "my_mongodb"
    5. then Nightscout Server would be "mongodb+srv://my_mongo_user:my_mongo_pass@ds123.mongodb.net/my_mongo_db?retryWrites=true&w=majority"
    (Note that ds123.mongodb.net will be different for your own set up)
    7. Nightscout DB would be "my_mongo_db"
    8. Nightscout Collection is always �treatments�
12.  Once the data is loaded, an analysis automatically runs but a more detailed report can be generated by selecting **Action** menu and then **Analyze Results**
13.  Select the file type to load (it may show Cell Novo initially)
10.  Navigate to the file to load by clicking the **Select** button
11.  Start the synchronisation by selecting **Action** menu and then **Synchronise**
12.  Once the data is loaded, an analysis automatically runs but a more detailed report can be generated by selecting **Action** menu and then **Analyze Results**
To convert a previous installation for non-Nightscout to one for Nightscout use, simply complete steps 5 and onwards above, save the settings and re-launch the application.

### Installation Guide for non-Nightscout use on Windows

1. Check your version of Java at this [link](https://java.com/en/download/help/version_manual.xml)
1. Download the application from the [Github Releases](https://github.com/gh-davidr/NightscoutLoader/releases) page.
    1. Occassionally, an early beta version is made available with fixes on the stable version or access to upcoming features.
2. Copy the **NightScoutLoader.jar** and **NightScoutLoader.bat** files to your desktop (or any other location), ensuring that both files **are in the same folder**.
    1. (Note that the loadLibreview.sh and loadDiasend.sh files are template Linux files for running NightscoutLoader as a batch)
3. Launch the application by double-clicking the **NightScoutLoader.bat** file
    1. The **NightScoutLoader.bat** script checks the jar file is correctly located.  If it is not correctly located, then it will alert and exit.
    1. It will also attempt to create C:\Temp folder for the main application log file.  If this attempt fails then it will alert and exit.
3. Launch the application by double-clicking the **NightScoutLoader.bat** file
9.  Select the file type to load (it may show Cell Novo initially)
10.  Navigate to the file to load by clicking the **Select** button
11.  Load the file by selecting **Action** menu and then **Load Meter/Pump Only**
12.  Once the data is loaded, an analysis automatically runs but a more detailed report can be generated by selecting **Action** menu and then **Analyze Results**

### Installation Guide for Mac

1. Sorry - I don't have regular access to a Mac that would enable the provision of instructions for use.  
1. The application will definitely run on MacOS (as reported by existing MacOS users) and it should be sufficient to launch it from the **NightScoutLoader.jar** file without needing to have a run script.
1. Perhaps someone can advise.


### Obtaining the MongoDB URI from Heroku

Setting MongoDB for Nightscout Server:
1. Login to Heroku (https://id.heroku.com/login)
2. Click on your Nightscout application
3. Click on "Settings" tab
4. In "Config Vars", click on "Reveal Config Vars"
5. Scroll down to "MONGODB_URI"
6. Copy value to right - which starts with "mongodb+srv://........"
7. Paste this URI text into "Nightscout Server" of NightscoutLoader's Settings screen

### Running NightscoutLoader as a batch

NightscoutLoader can be run with arguments from the command line instructing it to synchronise data from a file (such as Diasend for example).  In this way, it can be incorporated into a more streamlined set up to speed the process of getting data into Nightscout from a downloaded file.

A couple of redacted Bash shell scripts are included in the bin folder which the author uses on Linux for this purpose.  They can be used to write equivalent Windows bat scripts if desired.


---

That's all there is to it!

To convert a previous installation for Nightscout to one for non-Nightscout use, simply clear the Nightscout Server field in the Settings window, save the settings and re-launch the application.

:couple: [Nightscout Loader Website](https://david50703.wixsite.com/nightscoutloader)
:couple: [Nightscout Loader Facebook Page](https://www.facebook.com/NightscoutLoader)

