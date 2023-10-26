<img src="images/koncierge-logo.svg" alt="Koncierge" style="float: right; margin-right: 10px; margin-left: 10px;  height: 150px" />

# IDE Setup - IntelliJ

## Step 1: Install IntelliJ

You can get it from https://www.jetbrains.com/idea/

The free Community Edition is sufficient for running Koncierge.

## Step 2: Clone the Koncierge source code

In IntelliJ, click **File** > **New** > **Project from Version Control...**

Paste the URL and select a parent directory.

Click **Clone**.

## Step 3: Build the project

Right-click on **koncierge** in the tree view and select **Maven** > **Reload project**.

On the right-hand side of the screen, select the **Maven** tab and then 
select **koncierge** > **Lifecycle** > **install** in the tree view.

Then click the green **Run Maven Build** button. 

You should see something like this in the output:

```shell
[INFO] --- install:3.1.0:install (default-install) @ koncierge ---
[INFO] Installing /Users/user/data/projects/koncierge/pom.xml to /Users/user/.m2/repository/tech/koncierge/koncierge/0.0.1-SNAPSHOT/koncierge-0.0.1-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO] 
[INFO] Diagram Generator API 0.0.1-SNAPSHOT ............... SUCCESS [  1.992 s]
[INFO] koncierge-core 0.0.1-SNAPSHOT ...................... SUCCESS [  3.563 s]
[INFO] koncierge-example1 0.1.0-SNAPSHOT .................. SUCCESS [  0.616 s]
[INFO] koncierge-example2 0.0.1-SNAPSHOT .................. SUCCESS [  1.252 s]
[INFO] koncierge 0.0.1-SNAPSHOT ........................... SUCCESS [  0.007 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```


