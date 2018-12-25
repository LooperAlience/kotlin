package chela.kotlin.validation

import org.junit.Test
import org.junit.Assert.*

class Ruletest{
    @Test fun typeTest(){
        assertTrue("int", ChRuleSet("int").check(3) == 3)
        assertTrue("float", ChRuleSet("float").check(3.1F) == 3.1F)
        assertTrue("long", ChRuleSet("long").check(3L) == 3L)
        assertTrue("double", ChRuleSet("double").check(3.5) == 3.5)
        assertTrue("char", ChRuleSet("char").check('1') == '1')
        assertTrue("string", ChRuleSet("string").check("abc") == "abc")
    }
    @Test fun baseTest(){
        assertTrue("MinLength", ChRuleSet("MinLength[4]").check("abcd") == "abcd")
        assertTrue("MinLength", ChRuleSet("MinLength[4]").check("abc") != "abc")
        assertTrue("MaxLength", ChRuleSet("MaxLength[4]").check("abcd") == "abcd")
        assertTrue("MaxLength", ChRuleSet("MaxLength[4]").check("abcde") != "abcde")
    }
}