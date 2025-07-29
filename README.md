# 🧾 LEVI for SNOMED

**LEVI for SNOMED** – *Language and Extension Validation & Import for SNOMED*

## 📝 Description

LEVI for SNOMED is a utility tool that acts as a bridge between the SNOMED International Authoring Tool and TermMed's Termspace. It facilitates the validation, comparison, and import of translation content into SNOMED CT extensions for German, French, and Italian. It supports various input formats and ensures consistency and accuracy in translation submissions.

## 🚀 Features

- Validation of translation terms before import
- Comparison with existing SNOMED extensions
- Import template generation for new or updated translations
- Detection and preparation of inactivation entries
- Regex-based language checks
- Support for CSV, TSV, and Excel files as input

## 🏁 Getting Started / Installation

### Prerequisites

- Java 17 or later
- Maven

### Installation Steps

```bash
git clone https://github.com/your-org/SNOMED_Applications.git
```
- Open Eclipse IDE
- Select "Import > Existing Projects into Workspace"
- Choose the cloned levi-for-snomed folder
- Run Main.java as a Java application

## 🧩 Architecture / Technical Overview

LEVI consists of the following major components:

- `Main.java`: Entry point of the application. Initializes the Compare class and triggers one of the comparison or analysis functions.
- `Compare.java`: Core logic controller. Manages internal lists (e.g., new translations, inactivations, synonyms) and orchestrates the entire flow of analysis, comparison, and result generation.
- `DB_connection.java`: Encapsulates all database communication using JDBC. Provides helper methods for querying.
- `ReadTranslation.java`: Parses CSV files containing translation data and passes the content to Compare via setter methods.
<img width="2508" height="964" alt="image" src="https://github.com/user-attachments/assets/c52e27ea-6f91-4369-b02a-9296c96f21ec" />

## ⚙️ Configuration

Adjustable settings:

- Database connection
- Language selection
- Input/output paths


## 📂 Folder / File Structure

```bash
/src
  /main
    /java
      Main.java
      TranslationChecker.java
      DeltaGenerator.java
      FileParser.java
      ExtensionLookup.java
```

## 📦 Dependencies

The project uses the following key libraries:

- **Apache Commons Lang 3**
  - For utilities like `Pair`
- **OpenCSV**
  - `CSVReader`, `CSVParserBuilder`, `CSVReaderBuilder`
- **Apache POI**
  - For Excel file support (`HSSFWorkbook`, `XSSFWorkbook`, `Sheet`, etc.)
- **JDBC (java.sql.\*)**
  - For database interaction
- **Java Core Libraries**
  - `java.io`, `java.nio.file`, `java.security`, `java.util`, `java.util.stream`

## 🧑‍💻 Contributing

Contributions are welcome. Please fork the repository and submit a pull request. If you're adding a feature, please open an issue first to discuss it.

## 🪲 Known Issues / Limitations

- No GUI (currently CLI-only)
- No multithreading for large files
- Only tested against a specific TermMed SNOMED extension setup
- No support for automatic rollback on import error

## 📄 License

This project is licensed under the MIT License.
