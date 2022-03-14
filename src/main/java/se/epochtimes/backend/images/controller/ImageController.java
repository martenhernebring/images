package se.epochtimes.backend.images.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.epochtimes.backend.images.dto.FileDTO;
import se.epochtimes.backend.images.model.BucketName;
import se.epochtimes.backend.images.service.ImageService;

import java.util.List;

@RestController("imageController")
@RequestMapping(value = "/v1/images")
public class ImageController {

  private final ImageService imageService;
  public final static String PREFIX = "/inrikes/2022/ekonomi/";

  @Autowired
  public ImageController(ImageService imageService) {
    this.imageService = imageService;
  }

  @Operation(summary = "Save an image.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200",
      description = "Successfully saved the image",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = FileDTO.class))),
    @ApiResponse(responseCode = "409",
      description = "Image has already been added",
      content = @Content),
    @ApiResponse(responseCode = "404",
      description = "The article has not been posted yet",
      content = @Content),
    @ApiResponse(responseCode = "400",
      description = "File had no content or wasn't an image",
      content = @Content)
  })
  @PostMapping(value = PREFIX + "{articleId}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public FileDTO postImage(@PathVariable String articleId, @RequestBody MultipartFile file) {
    return imageService.save(PREFIX + articleId, BucketName.ARTICLE_IMAGE, file);
  }

  @Operation(summary = "Get saved images list.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200",
      description = "Successfully saved the image",
      content = @Content(mediaType = "application/json",
        array = @ArraySchema(
          schema = @Schema(implementation = FileDTO.class))))
  })
  @GetMapping(value = "")
  public List<FileDTO> getAllUnsorted() {
    return imageService.getAllUnsorted();
  }

  @Operation(summary = "Download an image.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200",
      description = "Successfully downloaded the image",
      content = @Content(mediaType = "application/json",
        array = @ArraySchema(
          schema = @Schema(implementation = byte.class))))
  })
  @GetMapping(value = PREFIX + "{articleId}/{fileName}")
  public byte[] download(@PathVariable String articleId, @PathVariable String fileName) {
    return imageService.get(articleId, fileName);
  }
}
