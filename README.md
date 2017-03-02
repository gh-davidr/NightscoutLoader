# NightscoutLoader

## Why the name 'Nightscout' Loader?

* Because [Nightscout](http://www.nightscout.info/) is the CGM in the Cloud environment that this tool supports.

## What is Nightscout Loader?

A desktop application with the following purposes in mind:

* Synchronize data from meter/pump as treatments in Nightscout Care Portal.
* Quick way of locating and allowing edits to the Notes field in treatment data.
* Analysis of BG Trends
* Analysis of CGM Data
* Easier access to Autotune

### Synchronize data from meter/pump as treatments in Nightscout Care Portal.

![picture](resources/images/MainPage.jpg)

Meter & Pump Data can be regularly loaded from a range of systems:
  * Roche (SQL Server Database load as well as CSV export)
  * Medtronic (csv)
  * Diasend (xls)
  * Tandem t:slim (csv)

  
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

![picture](resources/images/AutotuneManagementRun.JPG)

As well as integrating Autotune output in the Excel analysis results file, an "Autotune Management" window allows easy access to run Autotune once configured.


![picture](resources/images/AutotuneManagementProfileEditor.JPG)

A Profile editor is provided that can edit the remote profile, open profile files on the local machine and also read profile data from a Diasend export.


![picture](resources/images/AutotuneManagementListBackups.JPG)

Each time the Profile Editor updates the profile on the remote Autotune server, it takes a backup into a separate directory.  The files in the backup directory can be listed (as above) and also downloaded onto the local machine for greater safe keeping.

## Analytic approach used by NightscoutLoader

![picture](resources/images/TrendAnalysis.jpg)

Nightscout Loader helps get real **Insights** from reams of *Data*.

## Is it for me?

- [x] Do you use Nightscout and want to upload Treatment data to Nightscout from a supported Meter and or Pump?
- [x] Do you want an extra tool to help analyze BGs, Carbs & Insulin?  (Nightscout use is optional)
- [x] Do you use Nightscout and want an extra tool to visualize repeated patterns of CGM data?
- [x] Do you have Autotune configured on a separate Linux machine and want assistance with using it - particularly if you also use Diasend?

If any of the above are true, then it could help you.

## Installation Guide

### System Requirements

The application is built for desktop or laptop computers with Java.  It requires at least Java version 7 and the installation guide includes a link to check and install if necessary.


It loads the Nightscout Treatment and CGM into memory for comparison with new data being uploaded or for analysis.  The amount of memory your system needs will depend on how much data you pull in.  As a guide, 8 years' worth of treatment data and 20,000 CGM entries takes up 300Mb of RAM.


### Installation Guide for Nightscout use

1. Check your version of Java at this [link] (https://java.com/en/download/help/version_manual.xml)
1. Download the application from Google Drive.  
  1. Latest stable version always available [here] (https://drive.google.com/open?id=0BxlKJmCnE32_bVlpd3pHVjJnTzQ). 
  1. Occassionally, an early beta version is made available with fixes on the stable version or access to upcoming 
features.  The [Latest Beta] (https://drive.google.com/open?id=0BxlKJmCnE32_MmxTdXZGaUEyYVE) folder will have the latest beta if one is available.
2. Copy the NightScoutLoader.jar file to your desktop
3. Launch the application by double-clicking the icon on your desktop.
4. Click Tools => Settings to launch the Settings panel
5. Complete the Sections in Yellow
6. For example
  1.  if your user is "my_mongo_user"
  2.  password is "my_mongo_pass"
  3.  port is 123
  4.  database is "my_mongodb"
  5.  then, please see how the Nightscout Server, Nightscout DB and Nightscout Collection fields are set:
    6.  Nightscout Server would "mongodb://my_mongo_user:my_mongo_pass@ds123.mongolab.com:123"
    7.  Nightscout DB would be "my_mongodb"
    8.  Nightscout Collection is always “treatments”


To convert a previous installation for non-Nightscout to one for Nightscout use, simply complete steps 5 and onwards above, save the settings and re-launch the application.

### Installation Guide for non-Nightscout use

1. Check your version of Java at this [link] (https://java.com/en/download/help/version_manual.xml)
1. Download the application from Google Drive.  
  1. Latest stable version always available [here] (https://drive.google.com/open?id=0BxlKJmCnE32_bVlpd3pHVjJnTzQ). 
  1. Occassionally, an early beta version is made available with fixes on the stable version or access to upcoming 
features.  The [Latest Beta] (https://drive.google.com/open?id=0BxlKJmCnE32_MmxTdXZGaUEyYVE) folder will have the latest beta if one is available.
2. Copy the NightScoutLoader.jar file to your desktop
3. Launch the application by double-clicking the icon on your desktop

That's all there is to it!

To convert a previous installation for Nightscout to one for non-Nightscout use, simply clear the Nightscout Server field in the Settings window, save the settings and re-launch the application.

:couple: [Nightscout Loader Facebook Page] (https://www.facebook.com/NightscoutLoader)

