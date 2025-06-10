package com.example.student3.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for implementing pagination in lists and data sets.
 * 
 * Features:
 * - Generic pagination support for any data type
 * - Configurable page sizes
 * - Memory-efficient data loading
 * - Page navigation utilities
 * 
 * @author DANN4 Development Team
 * @version 1.0
 * @since 2024
 */
public class PaginationUtils {
    
    // Default pagination settings
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int COURSES_PAGE_SIZE = 15;
    public static final int ANNOUNCEMENTS_PAGE_SIZE = 5;
    public static final int STUDENTS_PAGE_SIZE = 20;
    
    /**
     * Represents a paginated result set.
     * 
     * @param <T> Type of items in the page
     */
    public static class PagedResult<T> {
        private final List<T> items;
        private final int currentPage;
        private final int totalPages;
        private final int totalItems;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        
        public PagedResult(List<T> items, int currentPage, int totalItems, int pageSize) {
            this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
            this.currentPage = currentPage;
            this.totalItems = totalItems;
            this.pageSize = pageSize;
            this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
            this.hasNext = currentPage < totalPages;
            this.hasPrevious = currentPage > 1;
        }
        
        // Getters
        public List<T> getItems() { return new ArrayList<>(items); }
        public int getCurrentPage() { return currentPage; }
        public int getTotalPages() { return totalPages; }
        public int getTotalItems() { return totalItems; }
        public int getPageSize() { return pageSize; }
        public boolean hasNext() { return hasNext; }
        public boolean hasPrevious() { return hasPrevious; }
        public boolean isEmpty() { return items.isEmpty(); }
        public int getItemCount() { return items.size(); }
        
        /**
         * Gets the starting item number for current page (1-based).
         */
        public int getStartItemNumber() {
            return totalItems == 0 ? 0 : (currentPage - 1) * pageSize + 1;
        }
        
        /**
         * Gets the ending item number for current page (1-based).
         */
        public int getEndItemNumber() {
            return Math.min(currentPage * pageSize, totalItems);
        }
    }
    
    /**
     * Creates a paginated result from a complete data set.
     * 
     * @param allItems Complete list of items
     * @param page Page number (1-based)
     * @param pageSize Number of items per page
     * @param <T> Type of items
     * @return Paginated result
     */
    public static <T> PagedResult<T> paginate(List<T> allItems, int page, int pageSize) {
        if (allItems == null || allItems.isEmpty()) {
            return new PagedResult<>(new ArrayList<>(), page, 0, pageSize);
        }
        
        int totalItems = allItems.size();
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        
        // Validate page bounds
        if (startIndex >= totalItems || startIndex < 0) {
            return new PagedResult<>(new ArrayList<>(), page, totalItems, pageSize);
        }
        
        List<T> pageItems = allItems.subList(startIndex, endIndex);
        return new PagedResult<>(pageItems, page, totalItems, pageSize);
    }
    
    /**
     * Creates a paginated result for courses with default page size.
     */
    public static <T> PagedResult<T> paginateCourses(List<T> courses, int page) {
        return paginate(courses, page, COURSES_PAGE_SIZE);
    }
    
    /**
     * Creates a paginated result for announcements with default page size.
     */
    public static <T> PagedResult<T> paginateAnnouncements(List<T> announcements, int page) {
        return paginate(announcements, page, ANNOUNCEMENTS_PAGE_SIZE);
    }
    
    /**
     * Creates a paginated result for students with default page size.
     */
    public static <T> PagedResult<T> paginateStudents(List<T> students, int page) {
        return paginate(students, page, STUDENTS_PAGE_SIZE);
    }
    
    /**
     * Calculates the total number of pages for given items and page size.
     */
    public static int calculateTotalPages(int totalItems, int pageSize) {
        if (totalItems <= 0 || pageSize <= 0) return 0;
        return (int) Math.ceil((double) totalItems / pageSize);
    }
    
    /**
     * Validates if a page number is valid for given total pages.
     */
    public static boolean isValidPage(int page, int totalPages) {
        return page >= 1 && page <= Math.max(1, totalPages);
    }
    
    /**
     * Gets the next page number, or current page if no next page.
     */
    public static int getNextPage(int currentPage, int totalPages) {
        return currentPage < totalPages ? currentPage + 1 : currentPage;
    }
    
    /**
     * Gets the previous page number, or current page if no previous page.
     */
    public static int getPreviousPage(int currentPage) {
        return currentPage > 1 ? currentPage - 1 : currentPage;
    }
    
    /**
     * Creates page range for pagination UI (e.g., [1, 2, 3, 4, 5]).
     * 
     * @param currentPage Current page number
     * @param totalPages Total number of pages
     * @param maxVisible Maximum number of page numbers to show
     * @return List of page numbers to display
     */
    public static List<Integer> getPageRange(int currentPage, int totalPages, int maxVisible) {
        List<Integer> pages = new ArrayList<>();
        
        if (totalPages <= maxVisible) {
            // Show all pages
            for (int i = 1; i <= totalPages; i++) {
                pages.add(i);
            }
        } else {
            // Calculate range around current page
            int start = Math.max(1, currentPage - maxVisible / 2);
            int end = Math.min(totalPages, start + maxVisible - 1);
            
            // Adjust start if we're near the end
            if (end - start + 1 < maxVisible) {
                start = Math.max(1, end - maxVisible + 1);
            }
            
            for (int i = start; i <= end; i++) {
                pages.add(i);
            }
        }
        
        return pages;
    }
    
    /**
     * Merges multiple pages of data (useful for infinite scrolling).
     * 
     * @param existingItems Current items
     * @param newPage New page to append
     * @param <T> Type of items
     * @return Combined list
     */
    public static <T> List<T> mergePages(List<T> existingItems, PagedResult<T> newPage) {
        List<T> merged = new ArrayList<>();
        if (existingItems != null) {
            merged.addAll(existingItems);
        }
        if (newPage != null && !newPage.isEmpty()) {
            merged.addAll(newPage.getItems());
        }
        return merged;
    }
    
    /**
     * Gets pagination info string for UI display.
     */
    public static String getPaginationInfo(PagedResult<?> result) {
        if (result.isEmpty()) {
            return "No items found";
        }
        
        return String.format("Showing %d-%d of %d items (Page %d of %d)",
                result.getStartItemNumber(),
                result.getEndItemNumber(),
                result.getTotalItems(),
                result.getCurrentPage(),
                result.getTotalPages());
    }
}
