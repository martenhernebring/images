package se.epochtimes.backend.images.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.model.Subject;
import se.epochtimes.backend.images.service.FileService;

import java.io.IOException;

@RestController("fileController")
@RequestMapping(value = "/v1/images")
public class FileController {

  final FileService fileService;

  @Autowired
  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  @Operation(summary = "Save an image.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200",
      description = "Successfully saved the article",
      content = @Content)
  })
  @PostMapping(
    value = "/inrikes/2022/ekonomi/{articleId}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public void postImage(@PathVariable String articleId, @RequestBody MultipartFile file) {
    var hc = new HeaderComponent(Subject.EKONOMI, 2022, "ekonomi", articleId);
    fileService.save(hc, BucketName.ARTICLE_IMAGE, file);
  }
}
