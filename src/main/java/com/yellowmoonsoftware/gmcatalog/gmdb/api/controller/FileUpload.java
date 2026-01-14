package com.yellowmoonsoftware.gmcatalog.gmdb.api.controller;

import org.springframework.http.codec.multipart.FilePart;

public record FileUpload (String type, FilePart file) {  }
