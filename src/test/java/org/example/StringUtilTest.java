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

    // ==================== Specific Word Rules: matrix/vertex/index ====================

    @Test
    void testMatrixRule() {
        assertEquals("matrices", StringUtil.pluralize("matrix"));
    }

    @Test
    void testVertexRule() {
        assertEquals("vertices", StringUtil.pluralize("vertex"));
    }

    @Test
    void testIndexRule() {
        assertEquals("indices", StringUtil.pluralize("index"));
    }

    // ==================== Y-Rule Tests ====================

    @Test
    void testConsonantPlusY() {
        // "quality" -> "qualities" (consonant 't' before 'y')
        assertEquals("qualities", StringUtil.pluralize("quality"));
    }

    @Test
    void testVowelPlusY_Boy() {
        // "boy" -> "boys" (vowel 'o' before 'y')
        assertEquals("boys", StringUtil.pluralize("boy"));
    }

    @Test
    void testVowelPlusY_Day() {
        // "day" -> "days" (vowel 'a' before 'y')
        assertEquals("days", StringUtil.pluralize("day"));
    }

    @Test
    void testVowelPlusY_Key() {
        // "key" -> "keys" (vowel 'e' before 'y')
        assertEquals("keys", StringUtil.pluralize("key"));
    }

    @Test
    void testVowelPlusY_Toy() {
        // "toy" -> "toys" (vowel 'o' before 'y')
        assertEquals("toys", StringUtil.pluralize("toy"));
    }

    @Test
    void testVowelPlusY_Guy() {
        // "guy" -> "guys" (vowel 'u' before 'y')
        assertEquals("guys", StringUtil.pluralize("guy"));
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
