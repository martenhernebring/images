package se.epochtimes.backend.images.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.MetaDTO;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.model.HeaderComponent;
import se.epochtimes.backend.images.service.ImageService;

@RestController("fileController")
@RequestMapping(value = "/v1/images")
public class FileController {

  final ImageService imageService;

  @Autowired
  public FileController(ImageService imageService) {
    this.imageService = imageService;
  }

  @Operation(summary = "Save an image.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200",
      description = "Successfully saved the image",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = MetaDTO.class)))
  })
  @PostMapping(
    value = "/inrikes/2022/ekonomi/{articleId}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public MetaDTO postImage(@PathVariable String articleId, @RequestBody MultipartFile file) {
    var hc = new HeaderComponent("ekonomi", 2022, "ekonomi", articleId);
    return imageService.save(hc, BucketName.ARTICLE_IMAGE, file);
  }
}
