package com.lightspeedhq.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the {@link CopyUtils} utility class.
 */
public class CopyUtilsTest {

    @Nested
    @DisplayName("Basic Types Tests")
    class BasicTypesTests {
        
        @Test
        @DisplayName("Null object should return null")
        void testNullObject() throws Exception {
            assertNull(CopyUtils.deepCopy(null), "Deep copy of null should be null");
        }
        
        @Test
        @DisplayName("Immutable objects should be handled correctly")
        void testImmutableObjects() throws Exception {
            // String test
            String originalString = "Test String";
            String copiedString = CopyUtils.deepCopy(originalString);
            assertEquals(originalString, copiedString, "Deep copy of String should equal original");
            
            // Integer test
            Integer originalInteger = 42;
            Integer copiedInteger = CopyUtils.deepCopy(originalInteger);
            assertEquals(originalInteger, copiedInteger, "Deep copy of Integer should equal original");
            
            // Boolean test
            Boolean originalBoolean = Boolean.TRUE;
            Boolean copiedBoolean = CopyUtils.deepCopy(originalBoolean);
            assertEquals(originalBoolean, copiedBoolean, "Deep copy of Boolean should equal original");
        }
        
        @Test
        @DisplayName("Primitive arrays should be deep copied")
        void testPrimitiveArrays() throws Exception {
            // Integer array
            Integer[] originalArray = new Integer[] {1, 2, 3, 4, 5};
            Integer[] copiedArray = CopyUtils.deepCopy(originalArray);
            
            assertNotSame(originalArray, copiedArray, "Copied array should be a different instance");
            assertArrayEquals(originalArray, copiedArray, "Array contents should be equal");
            
            // Modify original
            originalArray[0] = 99;
            assertNotEquals(originalArray[0], copiedArray[0], "Modifying original array should not affect copy");
        }
    }
    
    @Nested
    @DisplayName("Collection Tests")
    class CollectionTests {
        
        @Test
        @DisplayName("ArrayList should be deep copied")
        void testArrayList() throws Exception {
            // Create original list
            List<String> originalList = new ArrayList<>(Arrays.asList("One", "Two", "Three"));
            
            // Deep copy
            List<String> copiedList = CopyUtils.deepCopy(originalList);
            
            // Verify
            assertNotSame(originalList, copiedList, "Copied list should be a different instance");
            assertEquals(originalList, copiedList, "List contents should be equal");
            
            // Modify original
            originalList.add("Four");
            originalList.set(0, "Changed");
            
            // Verify independence
            assertEquals(3, copiedList.size(), "Copied list size should remain unchanged");
            assertEquals("One", copiedList.get(0), "Copied list elements should remain unchanged");
            assertFalse(copiedList.contains("Four"), "Copied list should not contain new elements from original");
        }
        
        @Test
        @DisplayName("HashSet should be deep copied")
        void testHashSet() throws Exception {
            // Create original set
            Set<String> originalSet = new HashSet<>(Arrays.asList("Apple", "Banana", "Cherry"));
            
            // Deep copy
            Set<String> copiedSet = CopyUtils.deepCopy(originalSet);
            
            // Verify
            assertNotSame(originalSet, copiedSet, "Copied set should be a different instance");
            assertEquals(originalSet, copiedSet, "Set contents should be equal");
            
            // Modify original
            originalSet.add("Date");
            originalSet.remove("Apple");
            
            // Verify independence
            assertEquals(3, copiedSet.size(), "Copied set size should remain unchanged");
            assertTrue(copiedSet.contains("Apple"), "Copied set should retain original elements");
            assertFalse(copiedSet.contains("Date"), "Copied set should not contain new elements from original");
        }
        
        @Test
        @DisplayName("HashMap should be deep copied")
        void testHashMap() throws Exception {
            // Create original map
            Map<String, Integer> originalMap = new HashMap<>();
            originalMap.put("One", 1);
            originalMap.put("Two", 2);
            originalMap.put("Three", 3);
            
            // Deep copy
            Map<String, Integer> copiedMap = CopyUtils.deepCopy(originalMap);
            
            // Verify
            assertNotSame(originalMap, copiedMap, "Copied map should be a different instance");
            assertEquals(originalMap, copiedMap, "Map contents should be equal");
            
            // Modify original
            originalMap.put("Four", 4);
            originalMap.put("One", 10);
            
            // Verify independence
            assertEquals(3, copiedMap.size(), "Copied map size should remain unchanged");
            assertEquals(Integer.valueOf(1), copiedMap.get("One"), "Copied map values should remain unchanged");
            assertFalse(copiedMap.containsKey("Four"), "Copied map should not contain new keys from original");
        }
    }
    
    @Nested
    @DisplayName("Complex Object Tests")
    class ComplexObjectTests {
        
        @Test
        @DisplayName("Simple object should be deep copied")
        void testSimpleObject() throws Exception {
            // Create original Man object
            Man originalMan = new Man("John Doe", 30, Arrays.asList("1984", "Brave New World"));
            
            // Deep copy
            Man copiedMan = CopyUtils.deepCopy(originalMan);
            
            // Verify
            assertNotSame(originalMan, copiedMan, "Copied object should be a different instance");
            assertEquals(originalMan.getName(), copiedMan.getName(), "Name should be copied correctly");
            assertEquals(originalMan.getAge(), copiedMan.getAge(), "Age should be copied correctly");
            assertEquals(originalMan.getFavoriteBooks(), copiedMan.getFavoriteBooks(), "Favorite books should be copied correctly");
            
            // Modify original
            originalMan.setName("Jane Doe");
            originalMan.setAge(25);

            // Verify independence
            assertEquals("John Doe", copiedMan.getName(), "Name in copy should remain unchanged");
            assertEquals(30, copiedMan.getAge(), "Age in copy should remain unchanged");
        }
        
        @Test
        @DisplayName("Objects with circular references should be deep copied correctly")
        void testCircularReferences() throws Exception {
            // Create objects with circular references
            Department hr = new Department("HR");
            Department it = new Department("IT");
            
            // Create circular reference
            hr.setRelatedDepartment(it);
            it.setRelatedDepartment(hr);
            
            // Deep copy
            Department copiedHr = CopyUtils.deepCopy(hr);
            
            // Verify
            assertNotSame(hr, copiedHr, "Copied department should be a different instance");
            assertNotSame(hr.getRelatedDepartment(), copiedHr.getRelatedDepartment(), 
                    "Related department should be a different instance");
            
            assertEquals(hr.getName(), copiedHr.getName(), "Department name should be copied correctly");
            assertEquals(hr.getRelatedDepartment().getName(), copiedHr.getRelatedDepartment().getName(), 
                    "Related department name should be copied correctly");
            
            // Verify circular reference is maintained in the copy
            assertSame(copiedHr.getRelatedDepartment().getRelatedDepartment(), copiedHr, 
                    "Circular reference should be maintained in copy");
            
            // Modify original
            hr.setName("Human Resources");
            it.setName("Information Technology");
            
            // Verify independence
            assertEquals("HR", copiedHr.getName(), "Department name in copy should remain unchanged");
            assertEquals("IT", copiedHr.getRelatedDepartment().getName(), 
                    "Related department name in copy should remain unchanged");
        }
        
        @Test
        @DisplayName("Objects with nested collections should be deep copied correctly")
        void testNestedCollections() throws Exception {
            // Create a complex structure
            Map<String, List<Man>> originalMap = new HashMap<>();
            
            List<Man> engineers = new ArrayList<>();
            engineers.add(new Man("John", 30, Arrays.asList("Code Complete", "Clean Code")));
            engineers.add(new Man("Jane", 28, Arrays.asList("Design Patterns", "Refactoring")));
            
            List<Man> managers = new ArrayList<>();
            managers.add(new Man("Bob", 45, Arrays.asList("Good to Great", "The Lean Startup")));
            
            originalMap.put("Engineers", engineers);
            originalMap.put("Managers", managers);
            
            // Deep copy
            Map<String, List<Man>> copiedMap = CopyUtils.deepCopy(originalMap);
            
            // Verify structure
            assertNotSame(originalMap, copiedMap, "Copied map should be a different instance");
            assertEquals(originalMap.size(), copiedMap.size(), "Map size should be the same");
            
            // Verify nested objects
            List<Man> copiedEngineers = copiedMap.get("Engineers");
            assertNotSame(engineers, copiedEngineers, "Engineers list should be a different instance");
            assertEquals(engineers.size(), copiedEngineers.size(), "Engineers list size should be the same");
            
            // Verify independence by modifying the original
            engineers.get(0).setName("Modified John");
            engineers.add(new Man("New Engineer", 22, new ArrayList<>()));

            // Check that the copy wasn't affected
            assertEquals("John", copiedEngineers.get(0).getName(), "Engineer name in copy should remain unchanged");
            assertEquals(2, copiedEngineers.size(), "Engineers list size in copy should remain unchanged");
        }
    }
    
    /**
     * Test class representing a department with a circular reference.
     */
    static class Department {
        private String name;
        private Department relatedDepartment;
        
        public Department() {
        }
        
        public Department(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Department getRelatedDepartment() {
            return relatedDepartment;
        }
        
        public void setRelatedDepartment(Department relatedDepartment) {
            this.relatedDepartment = relatedDepartment;
        }
    }
}
