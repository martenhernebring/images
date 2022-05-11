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
import se.epochtimes.backend.images.service.ImageService;

import java.util.List;

@RestController("imageController")
@RequestMapping(value = "/v1/images")
public class ImageController {

  private final ImageService imageService;

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
    @ApiResponse(responseCode = "400",
      description = "File had no content or wasn't an image",
      content = @Content)
  })
  @PostMapping(value = "/{category}/{year}/{vignette}/{articleId}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public FileDTO postImage(@PathVariable String category,
                           @PathVariable String year,
                           @PathVariable String vignette,
                           @PathVariable String articleId,
                           @RequestBody MultipartFile file) {
    return imageService.save(category + "/" + year + "/" +
      vignette + "/" + articleId, file);
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
        schema = @Schema(type = "String", format = "byte")))
  })
  @GetMapping(value = "/{category}/{year}/{vignette}/{articleId}/{fileName}")
  public byte[] download(
      @PathVariable String category, @PathVariable String year,
      @PathVariable String vignette, @PathVariable String articleId,
      @PathVariable String fileName
  ) {
    return imageService.get(category + "/" + year + "/" +
      vignette + "/" + articleId, fileName);
  }

  @Operation(summary = "Delete a file path.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200",
      description = "Successfully deleted the file path", content = @Content)})
  @DeleteMapping(value = "")
  public void deleteFilePath(@RequestParam("path") String path) {
    imageService.deleteByFilePath(path);
  }

}
