/*
 * Box Platform API
 * [Box Platform](https://box.dev) provides functionality to provide access to content stored within [Box](https://box.com). It provides endpoints for basic manipulation of files and folders, management of users within an enterprise, as well as more complex topics such as legal holds and retention policies.
 *
 * OpenAPI spec version: 2.0.0
 * Contact: devrel@box.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ch.cyberduck.core.box.io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * The order in which a pagination is ordered
 */
@Schema(description = "The order in which a pagination is ordered")

public class ItemsOrder {
  @JsonProperty("by")
  private String by = null;

  /**
   * The direction to order by, either ascending or descending
   */
  public enum DirectionEnum {
    ASC("ASC"),
    DESC("DESC");

    private String value;

    DirectionEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static DirectionEnum fromValue(String text) {
      for (DirectionEnum b : DirectionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("direction")
  private DirectionEnum direction = null;

  public ItemsOrder by(String by) {
    this.by = by;
    return this;
  }

   /**
   * The field to order by
   * @return by
  **/
  @Schema(example = "type", description = "The field to order by")
  public String getBy() {
    return by;
  }

  public void setBy(String by) {
    this.by = by;
  }

  public ItemsOrder direction(DirectionEnum direction) {
    this.direction = direction;
    return this;
  }

   /**
   * The direction to order by, either ascending or descending
   * @return direction
  **/
  @Schema(example = "ASC", description = "The direction to order by, either ascending or descending")
  public DirectionEnum getDirection() {
    return direction;
  }

  public void setDirection(DirectionEnum direction) {
    this.direction = direction;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ItemsOrder itemsOrder = (ItemsOrder) o;
    return Objects.equals(this.by, itemsOrder.by) &&
        Objects.equals(this.direction, itemsOrder.direction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(by, direction);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ItemsOrder {\n");
    
    sb.append("    by: ").append(toIndentedString(by)).append("\n");
    sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
