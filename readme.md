#### A simple test of the [taxii2Lib](https://github.com/workingDog/Taxii2LibScala) Scala library.

Connects to the "https://limo.anomali.com/api/v1/taxii2" TAXII-2 server and ask for the STIX objects from a collection. 
The STIX objects are received in a bundle, 
they are then extracted and written to file.

To run the TestApp, get [SBT](https://www.scala-sbt.org/).

Then at a command prompt, type:

    sbt run
    
You should see a new file called "testfile" with the STIX objects in it.

    