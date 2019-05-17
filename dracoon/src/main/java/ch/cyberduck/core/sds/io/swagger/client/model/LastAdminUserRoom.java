/*
 * DRACOON
 * REST Web Services for DRACOON<br>Version: 4.11.2  - built at: 2019-05-03 12:34:42<br><br><a title='Developer Information' href='https://developer.dracoon.com'>Developer Information</a>&emsp;&emsp;<a title='Get SDKs on GitHub' href='https://github.com/dracoon'>Get SDKs on GitHub</a>
 *
 * OpenAPI spec version: 4.11.2
 *
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package ch.cyberduck.core.sds.io.swagger.client.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Room information
 */
@ApiModel(description = "Room information")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class LastAdminUserRoom {
    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("parentPath")
    private String parentPath = null;

    @JsonProperty("lastAdminInGroup")
    private Boolean lastAdminInGroup = null;

    @JsonProperty("parentId")
    private Long parentId = null;

    @JsonProperty("lastAdminInGroupId")
    private Long lastAdminInGroupId = null;

    public LastAdminUserRoom id(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Room ID
     *
     * @return id
     **/
    @ApiModelProperty(required = true, value = "Room ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LastAdminUserRoom name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Room name
     *
     * @return name
     **/
    @ApiModelProperty(required = true, value = "Room name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LastAdminUserRoom parentPath(String parentPath) {
        this.parentPath = parentPath;
        return this;
    }

    /**
     * Parent node path &#x60;/&#x60; if node is a root node (room)
     *
     * @return parentPath
     **/
    @ApiModelProperty(required = true, value = "Parent node path `/` if node is a root node (room)")
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public LastAdminUserRoom lastAdminInGroup(Boolean lastAdminInGroup) {
        this.lastAdminInGroup = lastAdminInGroup;
        return this;
    }

    /**
     * Determines whether user is last admin of a room due to being the last member of last admin group
     *
     * @return lastAdminInGroup
     **/
    @ApiModelProperty(required = true, value = "Determines whether user is last admin of a room due to being the last member of last admin group")
    public Boolean isLastAdminInGroup() {
        return lastAdminInGroup;
    }

    public void setLastAdminInGroup(Boolean lastAdminInGroup) {
        this.lastAdminInGroup = lastAdminInGroup;
    }

    public LastAdminUserRoom parentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    /**
     * Parent room ID
     *
     * @return parentId
     **/
    @ApiModelProperty(value = "Parent room ID")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LastAdminUserRoom lastAdminInGroupId(Long lastAdminInGroupId) {
        this.lastAdminInGroupId = lastAdminInGroupId;
        return this;
    }

    /**
     * ID of the last admin group where the user is the only remaining member (returned only if &#x60;lastAdminInGroup&#x60; is &#x60;true&#x60;)
     *
     * @return lastAdminInGroupId
     **/
    @ApiModelProperty(value = "ID of the last admin group where the user is the only remaining member (returned only if `lastAdminInGroup` is `true`)")
    public Long getLastAdminInGroupId() {
        return lastAdminInGroupId;
    }

    public void setLastAdminInGroupId(Long lastAdminInGroupId) {
        this.lastAdminInGroupId = lastAdminInGroupId;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        LastAdminUserRoom lastAdminUserRoom = (LastAdminUserRoom) o;
        return Objects.equals(this.id, lastAdminUserRoom.id) &&
            Objects.equals(this.name, lastAdminUserRoom.name) &&
            Objects.equals(this.parentPath, lastAdminUserRoom.parentPath) &&
            Objects.equals(this.lastAdminInGroup, lastAdminUserRoom.lastAdminInGroup) &&
            Objects.equals(this.parentId, lastAdminUserRoom.parentId) &&
            Objects.equals(this.lastAdminInGroupId, lastAdminUserRoom.lastAdminInGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parentPath, lastAdminInGroup, parentId, lastAdminInGroupId);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LastAdminUserRoom {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    parentPath: ").append(toIndentedString(parentPath)).append("\n");
        sb.append("    lastAdminInGroup: ").append(toIndentedString(lastAdminInGroup)).append("\n");
        sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
        sb.append("    lastAdminInGroupId: ").append(toIndentedString(lastAdminInGroupId)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if(o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

