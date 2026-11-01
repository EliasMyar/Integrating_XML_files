# XML Integration Project: Scholar Researchers

A Java pipeline that reads an XML file of researchers (authors, interests, investigators and their articles), lets the user filter it, computes statistics over the result, and finally renders everything as an HTML page.

The pipeline is made of three programs that **must be run in this order**:

```
sample.xml ──► Selector ──► selector_output.xml ──► Processor ──► processor_output.xml ──► XMLtoHTML ──► final_output.html
```

| Step | Class | Reads | Writes |
|------|-------|-------|--------|
| 1 | `Selector` | `src/Test3/sample.xml` (validated against `src/Test3/trang_out.xsd`) | `selector_output.xml` |
| 2 | `Processor` | `selector_output.xml` (validated against `trang_out.xsd`) | `processor_output.xml` |
| 3 | `XMLtoHTML` | `processor_output.xml` + `src/Test3/toHTML.xsl` | `final_output.html` |

All three classes live in the package `Test3.JAVAClasses`. The JAXB classes (`Scholar`, `Investigator`, `Article`, `Author`, `Interest`, `Statistics`, `TopPublication`, ...) are generated from the XML Schema into `Test3.JAVAClasses.generated`.

---

## Requirements

- Java 17 or newer
- JAXB (Jakarta XML Binding, `jakarta.xml.bind`) and a runtime implementation (e.g. `org.glassfish.jaxb:jaxb-runtime`)
- The generated JAXB classes in `Test3/JAVAClasses/generated` (see below if you need to regenerate them)

## Project layout

```
.
├── src/
│   └── Test3/
│       ├── sample.xml          # original input data
│       ├── trang_out.xsd       # XML Schema (generated with Trang, then refined)
│       ├── toHTML.xsl          # XSLT stylesheet used by XMLtoHTML
│       └── JAVAClasses/
│           ├── Selector.java
│           ├── Processor.java
│           ├── XMLtoHTML.java
│           └── generated/      # JAXB classes generated from the schema
├── selector_output.xml         # created by Selector
├── processor_output.xml        # created by Processor
└── final_output.html           # created by XMLtoHTML
```

> **Important:** the programs use relative paths, so run them **from the project root folder** (the one that contains `src/`). Running from another directory will cause "file not found" errors.

---

## How to run

You can run each class from your IDE (Eclipse / IntelliJ / VS Code) with the project root as the working directory, or from the command line. Remember the order: **Selector → Processor → XMLtoHTML**.

### 1. `Selector`: filter the investigators

```
Test3.JAVAClasses.Selector
```

Loads `src/Test3/sample.xml`, validates it against the schema and shows an interactive menu:

```
Select investigators by:
1 - Host institution
2 - Interests
3 - Number of citations
4 - No more filters, generate output.xml
```

| Option | What it does |
|--------|--------------|
| **1 - Host institution** | Lists every host institution found in the data. Choose one by number; only investigators from that institution are kept. |
| **2 - Interests** | Lists all available interests. Enter one or more numbers separated by commas (e.g. `1,3`). An investigator is kept if they have **at least one** of the chosen interests. |
| **3 - Number of citations** | Asks (y/n) for an optional **minimum** and an optional **maximum** of total citations. The maximum cannot be lower than the minimum. |
| **4 - Generate output** | Applies all the chosen filters and writes the result. |

You can pick options 1-3 in any order, and repeat them (the last choice for each filter wins). Filters that were never chosen are simply not applied, so choosing option 4 straight away copies everything through.

**Output:** `selector_output.xml` in the project root, containing the filtered investigators together with the full `authors` and `interests` lists.

### 2. `Processor`: compute statistics

```
Test3.JAVAClasses.Processor
```

Reads and validates `selector_output.xml` (so **Selector must have been run first**) and computes:

- Total number of researchers
- Total number of distinct journals / venues
- Total number of citations (sum over all researchers)
- Investigator with the most citations in the current year
- Total number of publications
- Top **N** publications by citation count (the program asks you for N, between 1 and the total number of publications)

The results are printed to the console and also stored in a `statistics` element.

**Output:** `processor_output.xml`: the same data as `selector_output.xml` plus the new `statistics` section.

If the input file cannot be read, the program prints `Error in unmarshalling. Check file location.` and stops.

### 3. `XMLtoHTML`: generate the web page

```
Test3.JAVAClasses.XMLtoHTML
```

Applies the XSLT stylesheet `src/Test3/toHTML.xsl` to `processor_output.xml`. No user input is needed.

**Output:** `final_output.html` in the project root. Open it in any browser.

---

## Running from the command line (example)

Adjust the classpath to wherever your JAXB jars are.

```bash
# compile (from the project root)
javac -cp "libs/*" -d bin $(find src -name "*.java")

# run, in order
java -cp "bin:libs/*" Test3.JAVAClasses.Selector
java -cp "bin:libs/*" Test3.JAVAClasses.Processor
java -cp "bin:libs/*" Test3.JAVAClasses.XMLtoHTML
```

(On Windows use `;` instead of `:` as the classpath separator.)

---

## Regenerating the JAXB classes

If you change the schema, regenerate the classes with `xjc` into the `generated` package:

```bash
xjc -d src -p Test3.JAVAClasses.generated src/Test3/trang_out.xsd
```

---

## Troubleshooting

| Problem | Likely cause |
|---------|--------------|
| `FileNotFoundException` for `sample.xml` or `trang_out.xsd` | Program not run from the project root, or the files are not in `src/Test3/`. |
| `Error in unmarshalling. Check file location.` (Processor) | `selector_output.xml` does not exist yet. Run `Selector` first. |
| Schema validation errors | The XML does not conform to `trang_out.xsd`. Check IDs / references between authors, interests and investigators. |
| `Error in marshaling.` | The output file could not be written (permissions, or the file is open in another program). |

## Notes

- Cross-references in the XML are done through IDs: authors and interests carry an `id` attribute, and investigators/articles point to them. Investigators additionally use `authorRef` (IDREF) to reference their author.
- The statistics are computed only over the investigators that survived the `Selector` filters.
