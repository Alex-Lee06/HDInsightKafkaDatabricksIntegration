# HDInsight Kafka and Databricks Integration

Author: Alexander Lee

## Purpose
> The purpose of this How To Guide is to show Kafka and Databricks stream integration.  You will learn to integrate Kafka using HDI and stream data to Databricks.  This How To Guide will explain how to create a Virtual Network, Databricks workspace, HDInsight cluster and what is needed to bring in Kafka messages to a Databricks consumer.  We will be doing everything within the Azure portal and Databricks Notebook.

## Content
[**Purpose**][Purpose]<br>
[**Creating a Virtual Network and Subnets**](#Creating_a_Virtual_network)<br>
[**Creating a Databricks Service and WorkSpace**](#Creating-a-Databricks-Service-and-WorkSpace)<br>
[**Creating a HDInsight Kafka Cluster**](#Creating_a_HDInsight_Kafka_Cluster)<br>
[**Kafka Topic creation and messaging**](#Kafka_Topic_creation_and_messaging)<br>
[**Databricks Notebook and Code**](#Databricks_Notebook_and_Code)

## Prerequestise
1. Virtual Network
2. Azure Databricks 
3. HDInsight Kafka cluster
4. Databricks Notebook
5. Scala code

## Creating a Virtual network
[**Back to Content**](#content)

>A virtual network is needed so that communication between HDInsight and Databricks can be achieved.  In this How-To-Guide we will create 1 Virtual network and attach this to both the HDInsight cluster and Azure Databricks.


Let's start by creating a Virutal Network.
![Creating Virtual Network](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createVNet.png)

>Once a Virtual Network is created. We need to create 2 different subnets. 
1. Public subnet
2. Private subnet

For more information about Databricks Virtual Network please follow the below link.

https://learn.microsoft.com/en-us/azure/databricks/administration-guide/cloud-configurations/azure/vnet-inject

>Creating the 2 Subnets needed for Databricks.

![Creating Subnets](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createSubnets.png)

Once you have created your 2 Subnets we can now start creating our Databricks workspace.

## Creating an Azure Databricks workspace
[**Back to Content**](#content)

In Azure portal we will search for Azure Databricks to start creating Azure Databricks.

![Creating Azure Databricks](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/searchDatabricks.png)

Once you are on Azure Databricks in Azure portal you can create a new Azure Databricks Service.

![Creating Azure Databricks Service](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createService.png)

When starting to create the Databricks service you will get to the networking part of the Databricks workspace.  Here you need to make sure that you provide the Virtual Network and the Subnets you created up above.  The Virtual Network will also be used for the HDInsight cluster which we will show in the next section.

![Creating Azure Databricks workspace vnet](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/databricksVnet.png)

Once you have applied the Virtual Network and Subnets with the IPs you can leave everything else as default and "Review and Create"


![Creating Azure Databricks workspace vnet](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createWorkspace.png)

The Azure Databricks Service should take 5-10 mintues if there is no failures.

While this is being created we will look at creating a HDInsight Kafka Cluster.

## Creating a HDI Kafka cluster
[**Back to Content**](#content)

In Azure portal search for "HDInsight" in the search bar.

![Creating Azure Databricks workspace vnet](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/searchHDInsight.png)

Once you click on "HDInsight clusters" we can go ahead an click on "Create".

The next screen you will see is the "Basics" when creating a HDInsight cluster.

Fillin the necessary information and the version we will be using for Kafka is ***(Kafka 2.4.1 (HDI 5.0))***

![HDInsight Basic tab](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/basicHDInsight.png)

In the next section you will need to choose a storage.  For this How-To-Guide we will just use the default storages.

![HDInsight Default storage](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/defaultStorage.png)

Before going to the next section for Networking.  We need to go back to our Virtual Network and access the "default" Subnet.  Here we will need to add the general service tag for HDInsight and the region service tag for HDInsight.

In my case I will be adding HDInsight East since all my services in this How-To-Guide is created in East US.

![HDInsight NSG General service tag](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/addNSGrules.png)

Next we need to add in the regional tag to the NSG.

![HDInsight NSG regional service tag](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/regionalTag.png)

For more information about NSG rules for HDInsight you can follow the below link.

https://learn.microsoft.com/en-us/azure/hdinsight/hdinsight-service-tags

Once we have the Service tags added we can continue the cluster creation.

The next section will be for Networking of the HDInsight cluster.  The Virtual Network and Subnets were already created when we created the Virtual Network.  We will be applying those to this Network section for the HDInsight cluster.

![HDInsight Networking](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/HDINetworking.png)

In the next section "Configuration + pricing" we can leave all this as default settings.  There is no need for any changes.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/reviewcreatehdi.png)


The creation of the HDInsight Kafka cluster may take up to 30 minutes.

## Kafka Topic creation and messaging
[**Back to Content**](#content)

Once you have completed creating an Azure Databricks workspace and HDInsight Kafka cluster.  You will need to get more details from HDInsight, create a topic, and provide messages to the topic using a Kafka Producer.  In this section we will show you how to complete this.

The first thing we need to do is access the cluster using SSH.  For this example I will be using CMD.  You can use any command prompt or SSH tools.

To access the cluster you will need the following.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/sshHDI.png)

```ssh sshuser@<clustername>-ssh.azurehdinsight.net```

Once you have the ssh command copied we will copy it into CMD.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/sshcommand.png)

Once you hit enter, it will ask for a signed cert.  Type in "yes" and enter.  After that it will ask for the password for when you created the cluster.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/sshpassword.png)

When you type in the password you won't see anything.  You need to remember what you typed.

If you put in the correct password you will now see that you are within the cluster.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/loginssh.png)

Once you are in SSH session for the HDInsight cluster, we need to create a topic and send messages to the topic using a Kafka Producer.

First in SSH session, lets move to the correct location using linux commands.

```cd /usr/hdp/current/kafka-broker/bin```

Now before we go any farther, let's get the host IPs for the workernodes and zookeepers.  We will need this to create the topic and access the producer cli.

Using the following command we can get this information.

```cat /etc/hosts```

You will get the below information.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/etchostsfile.png)

Look for the internal IPs for wn# and zk#.  We will need that for the topic creation command and to open the producer cli.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/zkwnips.png)

Now that we have the IPs.  We need to create the topic that we will be producing messages to.

Taking the zookeeper IPs for all 3 and port 2181, we will use the topic sh script to create a test topic name **databricksTopic**.

```./kafka-topics.sh --create --zookeeper 10.0.0.8:2181,10.0.0.13:2181,10.0.0.11:2181 --replication-factor 1 --partitions 3 --topic databricksTopic```

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createTopic.png)

After creating the topic, let's go ahead an start the producer cli using the newly created topic.

Here we will need to use the workernode IPs with port 9092 to access the producer CLI

```./kafka-console-producer.sh --topic databricksTopic --broker-list 10.0.0.10:9092,10.0.0.14:9092,10.0.0.9:9092```

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/startProducer.png)

Once we start the producer, we will need to move over to Databricks and start the application.

## Databricks Notebook and Code
[**Back to Content**](#content)

Let's first access our Databricks UI.

In Azure portal search for ***Azure Databricks***

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/searchDatabricks.png)

Once you click on ***Azure Databricks*** it will take you to the Azure Databricks Service.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/clickService.png)

Now we can launch the workspace.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/launchWS.png)

To start with we first need to create a Databricks compute cluster.  We can do this as shown below.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createCompute.png)

Create the Databricks compute cluster.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/createDBRCluster.png)

We will leave all the ***defaults*** for this example when create the Databricks Compute cluster.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/defaultDBRCompute.png)

Once you start to create the Compute it will take a little bit of time for it to finish and start up.

While this is going on in the background, lets go ahead and start creating the Workspace.

We will jump straight into the code in the Databricks workspace we created using this How-To-Guide.  From there, create a Scala Notebook which we will use for the application.  

Creating a Databricks Workspace as below.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/dbrws.png)

We then click ***Add*** then click on ***Notebook***

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/addws.png)

A new screen will load up and this is your Databricks Notebook.

There are 2 things we need to do.  First we need to change the program language, then connect to the Databricks compute that we previously created.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/changeToScala.png)

Connect to Databricks Compute.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/attachCompute.png)

Now go back to your Notebook you just created and let us continue with the code.

Follow the link below for resources that will be used for this example.

>https://github.com/Alex-Lee06/HDInsightKafkaDatabricksIntegration

After creating a workspace and a compute for Databricks, you can copy the code in the below link.

>https://github.com/Alex-Lee06/HDInsightKafkaDatabricksIntegration/blob/main/connectToHbase.scala

There will be some changes that needs to be adjusted in the Scala code.

This piece of code needs to be changed to reflect your internal IPs of the workernodes for the HDInsight cluster.

```.option("kafka.bootstrap.servers", "10.0.0.7:9092,10.0.0.8:9092,10.0.0.6:9092")```

Changd to reflect my IPs:

My list of workernode IPs: 10.0.0.10:9092,10.0.0.14:9092,10.0.0.9:9092

```.option("kafka.bootstrap.servers", "10.0.0.10:9092,10.0.0.14:9092,10.0.0.9:9092")```

This needs to be changed to reflect your topic name:

```.option("subscribe", "databricksTopic")```

Everything else can stay the same, I recommend you put each piece of the case into a differnt cell.

You can see how I did it by downloading this: 

>https://github.com/Alex-Lee06/HDInsightKafkaDatabricksIntegration/blob/main/connectToHbase.html

Once you run each cell the last cell will be what you need to watch.

## Sending messages from HDInsight Kafka to Databricks.

In order to send data from HDInsight Kafka all you will have to do is copy the information here: 

>https://github.com/Alex-Lee06/HDInsightKafkaDatabricksIntegration/blob/main/person.json

Once you have it copied, paste that the data into the Kafka Producer CLI that should still be opend in your CMD.

Copy these data:

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/copyData.png)

```{"id":1,"firstname":"James ","middlename":"","lastname":"Smith","dob_year":2018,"dob_month":1,"gender":"M","salary":3000}
{"id":2,"firstname":"Michael ","middlename":"Rose","lastname":"","dob_year":2010,"dob_month":3,"gender":"M","salary":4000}
{"id":3,"firstname":"Robert ","middlename":"","lastname":"Williams","dob_year":2010,"dob_month":3,"gender":"M","salary":4000}
{"id":4,"firstname":"Maria ","middlename":"Anne","lastname":"Jones","dob_year":2005,"dob_month":5,"gender":"F","salary":4000}
{"id":5,"firstname":"Jen","middlename":"Mary","lastname":"Brown","dob_year":2010,"dob_month":7,"gender":"","salary":-1}
```

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/pasteToProducer.png)

Then press enter.  You will see it in the output of the last cell you ran in the Databricks Notebook.

![HDInsight Review Create](https://cssexamplesallee.blob.core.windows.net/tsgs/Other_MSFT_Components/Databricks/databicksOutput.png)

Although Spark is a streaming platform, it still does all computation in batches.  You will see the stream from Kafka in batches.

With this you have integrated HDInsight Kafka to Databricks and can stream data into Databricks.

If there are any questions please feel free to reach out to allee@microsoft.com

[**Back to Content**](#content)










[Purpose]: #Purpose
[Creating a Virtual Network and Subnets]: #Creating_a_Virtual_Network_and_Subnets
[Creating a Databricks Service and WorkSpace]: #Creating-a-Databricks-Service-and-WorkSpace
[Creating a HDInsight Kafka Cluster]: #Creating_a_HDInsight_Kafka_Cluster
[Kafka Topic creation and messaging]: #Kafka_Topic_creation_and_messaging
[Databricks Notebook and Code]: #Databricks_Notebook_and_Code
