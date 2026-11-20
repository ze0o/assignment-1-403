# 🧪 Unit Testing a Pluralization Utility with JUnit 5

Software testing assignment (University of Bahrain). The task: take an existing utility method,
`StringUtil.pluralize(String word)`, design a test suite for it from its rules, and find where it breaks.

![Java](https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit%205-25A162?logo=junit5&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?logo=apachemaven&logoColor=white)

---

## The code under test

`pluralize` turns an English noun into its plural by checking, in order:

1. **Invalid input**: a word containing punctuation throws `IllegalArgumentException`
2. **Uncountable words**: `sheep`, `fish`, `rice`, `information` … stay the same
3. **Irregular words**: `person → people`, `child → children`, `ox → oxen`
4. **Suffix rules**: `-sis → -ses`, `-o → -oes`, `-ium → -ia`, `-f → -ves`, `-x → -xes`, `-ch → -ches`, `-sh → -shes`,
   plus special words (`matrix → matrices`, `index → indices`, `axis → axes` …)
5. **Y rule**: consonant + `y` → `-ies` (`quality → qualities`), vowel + `y` → `-s` (`boy → boys`)
6. **Default**: add `s`

## Test design

The suite in [`StringUtilTest.java`](src/test/java/org/example/StringUtilTest.java) is organised by rule, so every
branch of the method is exercised at least once:

| Group | What it checks |
|---|---|
| Null and empty input | `null`, `""`, one-letter words |
| Uncountable words | Parameterized test over the whole list |
| Irregular words | Parameterized test over the whole list |
| Plural suffix rules | One test per suffix (`-sis`, `-s`, `-o`, `-ium`, `-f`, `-x`, `-ch`, `-sh`, `-man` …) |
| Specific word rules | `matrix`, `vertex`, `index`, `mouse`, `louse`, `quiz` … |
| Y rule | Consonant + y and every vowel + y |
| Default rule | Words that fall through to `+ s` |
| Invalid input | Parameterized test with punctuation characters |
| Case sensitivity | `CAT`, `Sheep` |

Techniques used: **equivalence partitioning** (one representative per rule), **boundary value analysis**
(empty and one-character words) and **parameterized tests** (`@ParameterizedTest` with `@CsvSource`,
`@ValueSource`, `@NullSource`).

## Bugs and weaknesses found

| Input | Behaviour | Why it matters |
|---|---|---|
| `""` (empty string) | throws `StringIndexOutOfBoundsException` | No guard for empty input |
| `"a"` (one letter) | throws `StringIndexOutOfBoundsException` | `charAt(l - 2)` reads index −1 |
| `"CAT"`, `"Sheep"` | `CATs`, `Sheeps` | Matching is case-sensitive, so capitalised words skip the rules |
| `"photo"`, `"piano"` | `photoes`, `pianoes` | The `-o → -oes` rule is too general |
| `"chef"`, `"roof"` | `cheves`, `rooves` | The `-f → -ves` rule is too general |
| `"human"` | `humen` | The `-man → -men` rule matches words that only end in "man" |

The tests document these as the method's *current* behaviour, so a fix would show up as a deliberate test change.

## Run it

Requires **JDK 25** and **Maven**.

```bash
git clone https://github.com/ze0o/assignment-1-403.git
cd assignment-1-403
mvn test
```

## Project structure

```
src/
├── main/java/org/example/
│   ├── StringUtil.java       code under test
│   └── Main.java
└── test/java/org/example/
    └── StringUtilTest.java   JUnit 5 test suite
pom.xml                       Maven build (JUnit Jupiter 5.10)
```

---

Made by **Abdulaziz Abdulhadi** · [GitHub](https://github.com/ze0o) · [LinkedIn](https://www.linkedin.com/in/abdulaziz-abdulhadi-5510a1332) · [Portfolio](https://3ziz-portfolio.netlify.app/)
