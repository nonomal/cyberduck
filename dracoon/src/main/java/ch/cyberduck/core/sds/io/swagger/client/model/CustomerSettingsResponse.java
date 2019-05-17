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
 * Customer settings
 */
@ApiModel(description = "Customer settings")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-05-17T14:22:07.810+02:00")
public class CustomerSettingsResponse {
    @JsonProperty("homeRoomsActive")
    private Boolean homeRoomsActive = null;

    @JsonProperty("homeRoomParentId")
    private Long homeRoomParentId = null;

    @JsonProperty("homeRoomParentName")
    private String homeRoomParentName = null;

    @JsonProperty("homeRoomQuota")
    private Long homeRoomQuota = null;

    public CustomerSettingsResponse homeRoomsActive(Boolean homeRoomsActive) {
        this.homeRoomsActive = homeRoomsActive;
        return this;
    }

    /**
     * Homerooms active
     *
     * @return homeRoomsActive
     **/
    @ApiModelProperty(required = true, value = "Homerooms active")
    public Boolean isHomeRoomsActive() {
        return homeRoomsActive;
    }

    public void setHomeRoomsActive(Boolean homeRoomsActive) {
        this.homeRoomsActive = homeRoomsActive;
    }

    public CustomerSettingsResponse homeRoomParentId(Long homeRoomParentId) {
        this.homeRoomParentId = homeRoomParentId;
        return this;
    }

    /**
     * Homeroom Parent ID
     *
     * @return homeRoomParentId
     **/
    @ApiModelProperty(value = "Homeroom Parent ID")
    public Long getHomeRoomParentId() {
        return homeRoomParentId;
    }

    public void setHomeRoomParentId(Long homeRoomParentId) {
        this.homeRoomParentId = homeRoomParentId;
    }

    public CustomerSettingsResponse homeRoomParentName(String homeRoomParentName) {
        this.homeRoomParentName = homeRoomParentName;
        return this;
    }

    /**
     * Homeroom Parent Name
     *
     * @return homeRoomParentName
     **/
    @ApiModelProperty(value = "Homeroom Parent Name")
    public String getHomeRoomParentName() {
        return homeRoomParentName;
    }

    public void setHomeRoomParentName(String homeRoomParentName) {
        this.homeRoomParentName = homeRoomParentName;
    }

    public CustomerSettingsResponse homeRoomQuota(Long homeRoomQuota) {
        this.homeRoomQuota = homeRoomQuota;
        return this;
    }

    /**
     * Homeroom Quota in bytes
     *
     * @return homeRoomQuota
     **/
    @ApiModelProperty(value = "Homeroom Quota in bytes")
    public Long getHomeRoomQuota() {
        return homeRoomQuota;
    }

    public void setHomeRoomQuota(Long homeRoomQuota) {
        this.homeRoomQuota = homeRoomQuota;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerSettingsResponse customerSettingsResponse = (CustomerSettingsResponse) o;
        return Objects.equals(this.homeRoomsActive, customerSettingsResponse.homeRoomsActive) &&
            Objects.equals(this.homeRoomParentId, customerSettingsResponse.homeRoomParentId) &&
            Objects.equals(this.homeRoomParentName, customerSettingsResponse.homeRoomParentName) &&
            Objects.equals(this.homeRoomQuota, customerSettingsResponse.homeRoomQuota);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homeRoomsActive, homeRoomParentId, homeRoomParentName, homeRoomQuota);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CustomerSettingsResponse {\n");

        sb.append("    homeRoomsActive: ").append(toIndentedString(homeRoomsActive)).append("\n");
        sb.append("    homeRoomParentId: ").append(toIndentedString(homeRoomParentId)).append("\n");
        sb.append("    homeRoomParentName: ").append(toIndentedString(homeRoomParentName)).append("\n");
        sb.append("    homeRoomQuota: ").append(toIndentedString(homeRoomQuota)).append("\n");
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

