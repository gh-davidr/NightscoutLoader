select -- Now (as of 12 Jan 2014) BG results only.
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
--        convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed

        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        -- case (res.ResultDate) -- Put some logic in here to say whether it's Breakfast, Lunch, Dinner, Bed, Night
        case (res.DataTypeID)
          when 4   THEN CONVERT(NUMERIC(6,1), ROUND(res.DataValue / 18, 1)) -- BG are held in US units
          else          CONVERT(NUMERIC(6,1), ROUND(res.DataValue, 1))
        end as "Result",      
        case (res.DataTypeID)
          when 4   THEN 'BG'
          else          'Other'
        end as "ResultType",
        rq.CustomName as "MealType",
        NULL as "Duration"
   from ZZZ_DB_ZZZ.CsSchema.CsResult res,
        ZZZ_DB_ZZZ.CsSchema.CsResultResultQualifier rrq,
        ZZZ_DB_ZZZ.CsSchema.CsResultQualifier rq
  where res.DataTypeID = 4
    and res.ResultID = rrq.ResultID
    and rrq.ResultQualifierID = rq.ResultQualifierID
    and (res.DataTypeID <> 18 or (res.DataTypeID = 18 and rq.CustomName = 'MTR CMD'))

    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'

UNION -- Total Daily Insulin
select 
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
--        convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed

        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        -- case (res.ResultDate) -- Put some logic in here to say whether it's Breakfast, Lunch, Dinner, Bed, Night
        case (res.DataTypeID)
          when 4   THEN CONVERT(NUMERIC(6,1), ROUND(res.DataValue / 18, 1)) -- BG are held in US units
          else          CONVERT(NUMERIC(6,1), ROUND(res.DataValue, 1))
        end as "Result",      
        case (res.DataTypeID)
          when 4   THEN 'BG'
          when 18  THEN 'Pump Units'
          when 9   THEN 'Carbs'
          when 20  THEN 'Daily Units'
        end as "ResultType",
        NULL as "MealType",
        NULL as "Duration"
   from ZZZ_DB_ZZZ.CsSchema.CsResult res
  where res.DataTypeID = 20
    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'

UNION -- All insulin from Pump now (standard Bolus, 

 SELECT 
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
--        convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed

        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        
        case (rrq.ResultQualifierID)
          when 21   THEN CONVERT(NUMERIC(6,0), ROUND(res.DataValue, 0)) -- To 1 Decimal place for tmp basal
          when 22   THEN CONVERT(NUMERIC(6,0), ROUND(res.DataValue, 0)) -- To 1 Decimal place for tmp basal
          else          CONVERT(NUMERIC(6,1), ROUND(res.DataValue, 3)) -- To 1 Decimal place
        end as "Result",              
        
        case (rq.CustomName)
          when 'cdf.resq.upr.slowbolus.type.extended'     THEN 'Extended'
          when 'cdf.resq.upr.flag.temp.basal.real.start'  THEN 'Tmp Basal Start'
          when 'cdf.resq.upr.flag.temp.basal.real.stop'   THEN 'Tmp Basal Stop'
          when 'cdf.resq.upr.flag.slowbolus.real.start'   THEN 'Extended Bolus Start'
          when 'cdf.resq.upr.flag.slowbolus.real.stop'    THEN 'Extended Bolus Stop'
          when 'cdf.resq.upr.slowbolus.type.multiwave'    THEN 'Slow Bolus MultiWave'
          when 'cdf.resq.upr.bolus.type.multiwave'        THEN 'MultiWave'
          when 'cdf.resq.upr.bolus.type.scroll'           THEN 'Bolus Scroll'
          when 'cdf.resq.upr.bolus.type.standard'         THEN 'Standard Bolus'
          when 'MTR CMD'                                  THEN 'Meter Command'
          end as "ResultType", 
        NULL as "MealType",
        res.Duration as "Duration"
        
        -- rrq.ResultQualifierID
        
   from ZZZ_DB_ZZZ.CsSchema.CsResult res,
        ZZZ_DB_ZZZ.CsSchema.CsResultResultQualifier rrq,
        ZZZ_DB_ZZZ.CsSchema.CsResultQualifier rq
  where rrq.ResultQualifierID in (
      18, -- cdf.resq.upr.slowbolus.type.extended
      21, -- cdf.resq.upr.flag.temp.basal.real.start
      22, -- cdf.resq.upr.flag.temp.basal.real.stop
      23, -- cdf.resq.upr.flag.slowbolus.real.start
      24, -- cdf.resq.upr.flag.slowbolus.real.stop
      19, -- cdf.resq.upr.slowbolus.type.multiwave
      17, -- cdf.resq.upr.bolus.type.multiwave
      16, -- cdf.resq.upr.bolus.type.scroll
      14) -- cdf.resq.upr.bolus.type.standard
    -- res.DataTypeID in (4, 9, 18) and
    and res.ResultID = rrq.ResultID
    and rrq.ResultQualifierID = rq.ResultQualifierID
--  and  res.ResultTypeID = 6
   and rq.CustomName <> 'MTR CMD'  -- Filter out these dupes instructions from meter.
    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'

UNION -- Pen Bolus    

SELECT 
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
--        convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed

        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        CONVERT(NUMERIC(6,1), ROUND(res.DataValue, 1)) as "Result",      
        'Pen Units' as "ResultType",
        NULL as "MealType",
        NULL as "Duration"
  from ZZZ_DB_ZZZ.CsSchema.CsResult res
 where DataTypeID = 18
 AND
   NOT EXISTS 
     (
     SELECT 1 
       FROM ZZZ_DB_ZZZ.CsSchema.CsResultResultQualifier rrq
      WHERE res.ResultID = rrq.ResultID
     )    
    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'
     
UNION -- Carbs
select
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
--        convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed

        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        CONVERT(NUMERIC(6,1), ROUND(res.DataValue, 1)) as "Result",      
        case (res.DataTypeID)
          when 4   THEN 'BG'
          when 18  THEN 'Pump Units'
          when 9   THEN 'Carbs'
          end as "ResultType",
        NULL as "MealType",
        NULL as "Duration"
   from ZZZ_DB_ZZZ.CsSchema.CsResult res
  where res.DataTypeID in (9)
    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'

UNION -- Temp Basals
SELECT 
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
--        convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed

        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        CONVERT(NUMERIC(6,1), ROUND(res.DataValue, 0)) as "Result",      
        case (res.DataTypeID)
          when 4   THEN 'BG'
          when 18  THEN 'Pump Units'
          when 9   THEN 'Carbs'
          when 26  THEN 'Temp Basal'
          end as "ResultType",
        NULL as "MealType",
        res.Duration as "Duration"
   FROM ZZZ_DB_ZZZ.CsSchema.CsResult res
  WHERE ResultTypeID = 4
    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'
 
 UNION -- Basal Rate Changes
 SELECT 
        datepart(YEAR, res.ResultDate)  as "Year",
        datepart(MONTH, res.ResultDate) as "Month",
        datepart(DAY, res.ResultDate)   as "Day",
        datename(dw, res.ResultDate)    as "DayName",
        res.ResultDate                  as "Time", 
 --       convert(VARCHAR(20), res.ResultDate, 108) as "JustTime", -- Comment out when not needed
        case
          when (convert(VARCHAR(20), ResultDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), ResultDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), ResultDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), ResultDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), ResultDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), ResultDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
        CONVERT(NUMERIC(4,3), ROUND(res.DataValue, 3)) as "Result",      
        case (res.DataTypeID)
          when 19   THEN 'Basal Rate Change'
        end as "ResultType",
        NULL as "MealType",
        res.Duration as "Duration" 
   FROM ZZZ_DB_ZZZ.CsSchema.CsResult res
  WHERE res.DataTypeID = 19
   AND
   EXISTS
   (
     SELECT 1 
       FROM ZZZ_DB_ZZZ.CsSchema.CsResult
      WHERE DataTypeID = 19
        AND datediff(hour, ResultDate, res.ResultDate) = 1
        AND DataValue <> res.DataValue
   )
    and res.ResultDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'
   
 
 -- Pump On, Off, Alarms etc
 UNION
 SELECT
        datepart(YEAR, res.FlagDate)  as "Year",
        datepart(MONTH, res.FlagDate) as "Month",
        datepart(DAY, res.FlagDate)   as "Day",
        datename(dw, res.FlagDate)    as "DayName",
        res.FlagDate                  as "Time", 
--        convert(VARCHAR(20), res.FlagDate, 108) as "JustTime", -- Comment out when not needed
        case
          when (convert(VARCHAR(20), FlagDate, 108)) between '00:00:00' and '07:00:00' THEN 'LateNight'
          when (convert(VARCHAR(20), FlagDate, 108)) between '07:00:00' and '11:30:00' THEN 'Breakfast'
          when (convert(VARCHAR(20), FlagDate, 108)) between '11:30:00' and '16:00:00' THEN 'Lunch'
          when (convert(VARCHAR(20), FlagDate, 108)) between '16:00:00' and '20:00:00' THEN 'Dinner'
          when (convert(VARCHAR(20), FlagDate, 108)) between '20:00:00' and '21:00:00' THEN 'Bed'
          when (convert(VARCHAR(20), FlagDate, 108)) between '21:00:00' and '23:59:59' THEN 'EarlyNight'
          ELSE 'Rest of time'
        END as "TimeSlot",
       NULL as "Result",   
        replace(flagType.TypeKey,'cdf.dflag.upr.event.','') as "ResultType",
        NULL as "MealType",
        NULL as "Duration" 
 
   FROM  ZZZ_DB_ZZZ.CsSchema.CsDeviceFlagType flagType,
        ZZZ_DB_ZZZ.CsSchema.CsDeviceFlag     res
 WHERE res.DeviceFlagTypeID = flagType.DeviceFlagTypeID
     and res.FlagDate BETWEEN 'XXX_START_DATE_XXX' AND 'YYY_END_DATE_YYY'



