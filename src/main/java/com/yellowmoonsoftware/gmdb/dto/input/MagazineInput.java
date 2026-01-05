package com.yellowmoonsoftware.gmdb.dto.input;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;

import java.time.LocalDate;

public record MagazineInput(
        LocalDate pubDate,
        @NonNull PubIndexInput index,
        @NonNull MagazineIssueInput issueInfo) { }

