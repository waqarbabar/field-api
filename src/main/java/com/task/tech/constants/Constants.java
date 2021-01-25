package com.task.tech.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final Integer DEFAULT_PAGE_NUMBER = 0;
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    public static final String TOTAL_PAGES = "total_pages";
    public static final String TOTAL_ENTRIES = "total_entries";
    public static final String TAG_API = "Fields API";
}