# MightyMusicalMusersApp
Interface for the MightMusicalMusers database
### File Overview
```
MightyMusicalMusersApp
┣ 📂docs                 (Documents for programmers)
┣ 📂lib                  (Required PostgreSQL driver for JDBC)
┣ 📂src                  (Source code for the project)
┃  ┣ 📂daos              (Data Access Objects for CRUD)
┃  ┣ 📂models            (POJOs for User and others)
┃  ┗ 📂src               (Additional source files)
```

# Setup instructions
- Please use **JDK 23.0.2** to minimize any compatibility issues.
- <details>
    <summary>Please set the local port in DataGrip to '5433' for seamless integration (see image at the bottom).</summary> 

    - alternatively you may be able to ssh outside DataGrip, however this is untested:
    ```bat
        ssh -L 5433:127.0.0.1:5432 rituser@starbug.cs.rit.edu:22
    ```
    </details>
- Add the 'postgresql-42.7.5.jar' file as a dependency in File/Project Structure (if using intelliJ)
- Rename the 'TEMP-config.properties' to 'config.properties' file and use your school login to access DataGrip 
## Local Port Image Guide
<img src="docs/guide.png" width="700px">

just right click on the p32001_30\@127.0.0.1 and select properties