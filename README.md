# NightscoutLoader

## What is Nightscout Loader?

A desktop application with the following purposes in mind:

* Synchronize data from meter/pump as treatments in Nightscout Care Portal.
* Quick way of locating and allowing edits to the Notes field in treatment data.
* Analysis of BG Trends
* Analysis of CGM Data

### Synchronize data from meter/pump as treatments in Nightscout Care Portal.

![picture](resources/images/MainPage.jpg)

Meter & Pump Data can be regularly loaded from a range of systems:
  * Roche (SQL Server Database load as well as CSV export)
  * Medtronic
  * Diasend
A serial stream of BG, Carb and Insulin is intelligently grouped together into a Treatment.  The tool will ignore previously loaded data.  It will also identify contention between manually entered Care Portal entries and data from meter/pump.
  
### Quick way of locating and allowing edits to the Notes field in treatment data.

![picture](resources/images/FindModify.jpg)

### Analysis of BG Trends

![picture](resources/images/TreatmentTrendAnalysis.jpg)

Analysis can be run over selected date ranges and outputs to Excel for convenient archiving of analytical snapshots.
Analysis also runs in background on start up and provides a condensed summary of top 3 trends

### Analysis of CGM Data

![picture](resources/images/CGMTrendAnalysis.jpg)

Any CGM data available within selected analysis date range is also analyzed.  A heat map shows areas of high frequency profiles.

## Analytic approach used by NightscoutLoader

![picture](resources/images/TrendAnalysis.jpg)

Nightscout Loader helps get real **Insights** from reams of *Data*.

