# 🧾 LEVI for SNOMED

## 📌 Project Title

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
git clone https://github.com/your-org/levi-for-snomed.git
cd levi-for-snomed
mvn clean install
java -jar target/levi-for-snomed.jar
```

## 🧪 Usage

Run LEVI from the command line with the input file:

```bash
java -jar target/levi-for-snomed.jar --input translations.xlsx --lang fr
```

Optional flags:

- `--lang [de|fr|it]`: Specify language
- `--dry-run`: Simulates output without writing files

Example input and output files are available in the `/examples` folder.

## 🧩 Architecture / Technical Overview

LEVI consists of the following major components:

- `Main.java`: Entry point for CLI processing
- `TranslationChecker.java`: Performs validation and language checks
- `DeltaGenerator.java`: Generates delta import templates
- `FileParser.java`: Handles input in CSV/TSV/Excel format
- `ExtensionLookup.java`: Connects to SNOMED extension DB for comparison

A component diagram or UML class diagram can be added here for deeper technical documentation.

## ⚙️ Configuration

Configuration is managed via `config.properties` or CLI arguments.

Adjustable settings:

- Database connection string
- Language selection
- Input/output paths
- Regex patterns for term validation

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
/resources
  config.properties
examples
  input_sample.xlsx
  output_delta_import.csv
README.md
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
