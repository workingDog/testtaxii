#### A simple test of the [taxii2Lib](https://github.com/workingDog/Taxii2LibScala) Scala library.

Connects to the [freetaxii](https://github.com/freetaxii/freetaxii-server) and extract 
all STIX objects from a collection. The individual STIX objects are then written to file.

To run the TestApp, get [SBT](https://www.scala-sbt.org/).

Then at a command prompt, type:

    sbt run
    
You should see a new file called "testfile" with the STIX objects iin it.

    