package org.example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;

public class StringUtilTest {

    // ==================== Null and Empty Input ====================

    @Test
    void testNullInput() {
        assertNull(StringUtil.pluralize(null), "Null input should return null");
    }

    @Test
    void testEmptyString() {
        // Empty string has length 0, charAt(l-1) will throw StringIndexOutOfBoundsException
        // This reveals a boundary bug: the method does not guard against empty input
        assertThrows(StringIndexOutOfBoundsException.class, () -> StringUtil.pluralize(""));
    }

    @Test
    void testSingleCharacter() {
        // Single character string: charAt(l-2) will access index -1
        // This reveals another boundary bug for very short words
        assertThrows(StringIndexOutOfBoundsException.class, () -> StringUtil.pluralize("a"));
    }

    // ==================== Uncountable Words ====================

    @ParameterizedTest
    @CsvSource({
        "equipment, equipment",
        "information, information",
        "rice, rice",
        "money, money",
        "species, species",
        "series, series",
        "fish, fish",
        "sheep, sheep"
    })
    void testUncountableWords(String input, String expected) {
        assertEquals(expected, StringUtil.pluralize(input),
            input + " is uncountable and should be returned unchanged");
    }

    // ==================== Irregular Words ====================

    @ParameterizedTest
    @CsvSource({
        "person, people",
        "child, children"
    })
    void testIrregularWords(String input, String expected) {
        assertEquals(expected, StringUtil.pluralize(input),
            "Irregular word '" + input + "' should become '" + expected + "'");
    }

    // ==================== Plural Suffix Rules ====================

    @Test
    void testAxisRule() {
        assertEquals("axes", StringUtil.pluralize("axis"));
    }

    @Test
    void testOctopusRule() {
        assertEquals("octopi", StringUtil.pluralize("octopus"));
    }

    @Test
    void testVirusRule() {
        assertEquals("viri", StringUtil.pluralize("virus"));
    }

    @Test
    void testSisSuffix() {
        // Words ending in "sis" -> "ses" (e.g., diagnosis -> diagnoses)
        assertEquals("diagnoses", StringUtil.pluralize("diagnosis"));
    }

    @Test
    void testGenericSSuffix() {
        // Words ending in "s" -> "ses" (e.g., bus -> buses)
        // Note: "bus" ends in "s", matched by rule { "s", "ses" }
        assertEquals("buses", StringUtil.pluralize("bus"));
    }

    @Test
    void testOSuffix() {
        // Words ending in "o" -> "oes" (e.g., tomato -> tomatoes)
        assertEquals("tomatoes", StringUtil.pluralize("tomato"));
    }

    @Test
    void testIumSuffix() {
        // Words ending in "ium" -> "ia" (e.g., stadium -> stadia)
        assertEquals("stadia", StringUtil.pluralize("stadium"));
    }

    @Test
    void testFSuffix() {
        // Words ending in "f" -> "ves" (e.g., leaf -> leaves)
        assertEquals("leaves", StringUtil.pluralize("leaf"));
    }

    @Test
    void testOxSuffix() {
        // "ox" -> "oxen"
        assertEquals("oxen", StringUtil.pluralize("ox"));
    }

    @Test
    void testXSuffix() {
        // Words ending in "x" -> "xes" (e.g., box -> boxes)
        assertEquals("boxes", StringUtil.pluralize("box"));
    }

    @Test
    void testChSuffix() {
        // Words ending in "ch" -> "ches" (e.g., church -> churches)
        assertEquals("churches", StringUtil.pluralize("church"));
    }

    @Test
    void testShSuffix() {
        // Words ending in "sh" -> "shes" (e.g., bush -> bushes)
        assertEquals("bushes", StringUtil.pluralize("bush"));
    }

    @Test
    void testMouseRule() {
        assertEquals("mice", StringUtil.pluralize("mouse"));
    }

    @Test
    void testLouseRule() {
        assertEquals("lice", StringUtil.pluralize("louse"));
    }

    @Test
    void testManSuffix() {
        // "man" -> "men" (e.g., woman -> women)
        assertEquals("women", StringUtil.pluralize("woman"));
    }

    @Test
    void testQuizRule() {
        assertEquals("quizzes", StringUtil.pluralize("quiz"));
    }

    // ==================== Rule Ordering Bug: matrix/vertex/index ====================
    // These rules appear AFTER the generic "x" -> "xes" rule in the plural array,
    // so they are never reached. "matrix" ends with "x", which matches first.

    @Test
    void testMatrixRule_ExpectedToFail() {
        // BUG: "matrix" ends with "x" which matches rule { "x", "xes" } at index 9
        // before reaching { "matrix", "matrices" } at index 12.
        // Actual result: "matrixes" instead of "matrices"
        assertEquals("matrices", StringUtil.pluralize("matrix"),
            "BUG: 'matrix' rule is shadowed by generic 'x' rule due to ordering");
    }

    @Test
    void testVertexRule_ExpectedToFail() {
        // BUG: Same ordering issue as matrix
        assertEquals("vertices", StringUtil.pluralize("vertex"),
            "BUG: 'vertex' rule is shadowed by generic 'x' rule due to ordering");
    }

    @Test
    void testIndexRule_ExpectedToFail() {
        // BUG: Same ordering issue as matrix
        assertEquals("indices", StringUtil.pluralize("index"),
            "BUG: 'index' rule is shadowed by generic 'x' rule due to ordering");
    }

    // ==================== Y-Rule Tests ====================
    // BUG: The condition (c2 != 'a' || c2 != 'e' || c2 == 'i' || c2 == 'o' || c2 == 'u')
    // is always true because (c2 != 'a' || c2 != 'e') is a tautology.
    // Correct condition should be:
    //   (c2 != 'a' && c2 != 'e' && c2 != 'i' && c2 != 'o' && c2 != 'u')

    @Test
    void testConsonantPlusY() {
        // "quality" -> "qualities" (consonant 't' before 'y')
        // This happens to work because the buggy condition is always true
        assertEquals("qualities", StringUtil.pluralize("quality"));
    }

    @Test
    void testVowelPlusY_Boy_ExpectedToFail() {
        // "boy" -> should be "boys" (vowel 'o' before 'y')
        // BUG: Returns "boies" because the Y-rule condition is always true
        assertEquals("boys", StringUtil.pluralize("boy"),
            "BUG: vowel+y words should just append 's', not replace y with ies");
    }

    @Test
    void testVowelPlusY_Day_ExpectedToFail() {
        // "day" -> should be "days" (vowel 'a' before 'y')
        assertEquals("days", StringUtil.pluralize("day"),
            "BUG: 'day' should become 'days', not 'daies'");
    }

    @Test
    void testVowelPlusY_Key_ExpectedToFail() {
        // "key" -> should be "keys" (vowel 'e' before 'y')
        assertEquals("keys", StringUtil.pluralize("key"),
            "BUG: 'key' should become 'keys', not 'keies'");
    }

    @Test
    void testVowelPlusY_Toy_ExpectedToFail() {
        // "toy" -> should be "toys"
        assertEquals("toys", StringUtil.pluralize("toy"),
            "BUG: 'toy' should become 'toys', not 'toies'");
    }

    @Test
    void testVowelPlusY_Guy_ExpectedToFail() {
        // "guy" -> should be "guys" (vowel 'u' before 'y')
        assertEquals("guys", StringUtil.pluralize("guy"),
            "BUG: 'guy' should become 'guys', not 'guies'");
    }

    // ==================== Default Rule (append 's') ====================

    @Test
    void testDefaultPluralAppendS() {
        assertEquals("cats", StringUtil.pluralize("cat"));
    }

    @Test
    void testDefaultPluralDog() {
        assertEquals("dogs", StringUtil.pluralize("dog"));
    }

    @Test
    void testDefaultPluralCar() {
        assertEquals("cars", StringUtil.pluralize("car"));
    }

    // ==================== Punctuation / Invalid Input ====================

    @ParameterizedTest
    @ValueSource(strings = {
        "hello!",
        "word@123",
        "comma,test",
        "semi;colon",
        "paren(thesis",
        "dollar$sign",
        "percent%age",
        "hash#tag",
        "plus+minus",
        "equal=sign",
        "back\\slash",
        "curly{brace",
        "pipe|line",
        "tilde~wave",
        "caret^top",
        "star*fish",
        "dot.com",
        "slash/mark",
        "angle<bracket",
        "question?mark",
        "colon:test",
        "quote\"test",
        "bracket[test"
    })
    void testPluralizeWithPunctuation(String invalidWord) {
        assertThrows(IllegalArgumentException.class,
            () -> StringUtil.pluralize(invalidWord),
            "Words with punctuation should throw IllegalArgumentException: " + invalidWord);
    }

    // ==================== Case Sensitivity ====================

    @Test
    void testUpperCaseWord() {
        // The method does not handle uppercase; "CAT" won't match any rules
        // and falls through to the Y-rule / default append 's'
        String result = StringUtil.pluralize("CAT");
        assertEquals("CATs", result, "Uppercase words just get 's' appended");
    }

    @Test
    void testMixedCaseUncountable() {
        // "Sheep" (capitalized) won't match "sheep" in the uncountable list
        // so it will be treated as a regular word
        assertNotEquals("Sheep", StringUtil.pluralize("Sheep"),
            "Case-sensitive matching means 'Sheep' is not recognized as uncountable");
    }

    @Test
    void testMixedCaseIrregular() {
        // "Child" won't match "child" in irregular list
        assertNotEquals("children", StringUtil.pluralize("Child"),
            "Case-sensitive matching means 'Child' is not recognized as irregular");
    }
}
