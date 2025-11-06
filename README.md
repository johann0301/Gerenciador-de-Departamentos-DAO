# Department Sales Manager

![JavaFX](https://img.shields.io/badge/Java-17%2B-blue.svg?logo=java&style=for-the-badge) ![JavaFX](https://img.shields.io/badge/JavaFX-17%2B-orange.svg?style=for-the-badge) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?logo=mysql&style=for-the-badge)

A complete desktop application built in Java for managing company departments and sellers. This project was developed to practice and demonstrate key software architecture patterns, including **MVC (Model-View-Controller)** and **DAO (Data Access Object)**.

---

## 🎥 Live Demo

*(This is a great place to insert your screen-recorded GIF or a short video!)*
`[Insert your .gif or .mp4 here]`

---

## ✨ Key Features

* **Department Management:**
    * Full CRUD (Create, Read, Update, Delete) operations.
    * **Integrity Constraint Validation:** Prevents the deletion of departments that still have sellers.
    * Real-time UI updates after any database change (using the Listener pattern).

* **Seller Management:**
    * Full CRUD (Create, Read, Update, Delete) operations.
    * Complex forms featuring `DatePicker` and `ComboBox` for data entry.
    * **Form Validation:** Ensures all required fields (Name, Email, Salary, etc.) are filled correctly.

* **Robust GUI:**
    * Built 100% with **JavaFX** and styled with FXML.
    * Multi-screen navigation system (Main Menu).
    * Modal (pop-up) windows for forms.
    * User-friendly confirmation dialogs and error alerts.

---

## 🏛️ Architecture

This project was built with a strong **Separation of Concerns** in mind:

1.  **Model-View-Controller (MVC) Pattern:**
    * **Model:** Our entities (`Seller`, `Department`) and the Data Access Layer (DAO).
    * **View:** The `FXML` layout files that define the user interface.
    * **Controller:** The `...Controller.java` classes that manage the UI logic and bridge the *View* and the *Model*.

2.  **Data Access Object (DAO) Pattern:**
    * All database access logic (`SellerDaoJDBC`, `DepartmentDaoJDBC`) is completely isolated from the business rules and the UI.
    * The application uses **JDBC** to communicate with a **MySQL** database.

3.  **Listener (Observer) Pattern:**
    * The form windows (e.g., `SellerFormController`) use a `DataChangeListener` interface to notify the list views (`SellerListController`) when data has changed, allowing the table to perform an automatic refresh.

---

## 🛠️ Tech Stack

* **Java 17+**
* **JavaFX 17+** (for the Graphical User Interface)
* **JDBC (Java Database Connectivity)**
* **MySQL** (Database)
* **MVC Design Pattern**
* **DAO Design Pattern**

---

## 🚀 How to Run

To run this project, you will need **IntelliJ IDEA** (or another Java IDE) and **MySQL Server**.

**1. Clone the Repository:**
bash
git clone [YOUR-GITHUB-REPOSITORY-URL-HERE]
cd project-name

**2. Set up the Database:**
* Open your MySQL client (Workbench, etc.).
* Run the `database.sql` script (or your named SQL file) located in the root of this project. This will create the required `department` and `seller` tables.

**3. Open in IntelliJ:**
* Open the project folder in IntelliJ IDEA.
* Add the required libraries to the project:
    * The **MySQL Connector/J** (`.jar` file).
    * The **JavaFX SDK** (v17+).
    * *IntelliJ Path:* `File` > `Project Structure...` > `Libraries` > `+` > `Java`.

**4. Configure `db.properties`:**
* Navigate to `src/db.properties`.
* Change the `db.url`, `db.user`, and `db.password` lines to match your local MySQL credentials.
    ```properties
    db.url=jdbc:mysql://localhost:3306/your_database_name
    db.user=your_user
    db.password=your_password
    ```

**5. (CRITICAL) Set JavaFX VM Options:**
* In IntelliJ, go to `Run` > `Edit Configurations...`.
* Click `+` and add a new `Application`.
* Name it "Run App".
* For **`Main class`**, select `application.Main`.
* Click `Modify options` > `Add VM options`.
* In the `VM options` field, paste the following line (adjust the path to your JavaFX SDK):
    ```
    --module-path "C:\path\to\your\javafx-sdk-21.0.9\lib" --add-modules javafx.controls,javafx.fxml
    ```

**6. Run!**
* Click the "Play" (▶️) button next to your "Run App" configuration.
