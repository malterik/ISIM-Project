
REM Scaling
For /L %%G IN (10 -2 1) DO For /L %%E IN (1 2 10) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 0.55 0.8 1 1 1 1 1 %%E %%G  


REM Probabilties
For %%G IN (0.3, 0.5 , 0.7 , 0.9) DO For  %%E IN (0.3, 0.5 , 0.7 , 0.9) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 %%G %%E 1 1 1 1 1 8 5

REM Weightings

REM Normal Tissue
FOR /L %%G IN (1 10 50) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 0.55 0.8 %%G 1 1 1 1 8 2 

REM Bladder
FOR /L %%G IN (1 10 50) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 0.55 0.8 1 %%G 1 1 1 8 2 


REM Rectum
FOR /L %%G IN (1 10 50) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 0.55 0.8 1 1 %%G  1 1 8 2 


REM Urethra
FOR /L %%G IN (1 10 50) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 0.55 0.8 1 1 1 %%G 1 8 2 


REM Prostate/Tumor
FOR /L %%G IN (1 10 50) DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 2 10 0.55 0.8 1 1 1 1 %%G  8 2

REM Population / Elitism
For /L %%G IN (0 2 10) DO For /L %%E IN (10 5 30)DO java -jar -Xmx16384m treatmentPlanner.jar GA 50 %%G %%E 0.55 0.8 1 1 1 1 1 8 2
