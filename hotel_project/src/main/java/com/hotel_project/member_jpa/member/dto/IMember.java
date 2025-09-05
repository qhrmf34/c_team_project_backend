package com.hotel_project.member_jpa.member.dto;

import java.time.LocalDateTime;

public interface IMember {
    Long getId();
    void setId(Long id);

    String getFirstName();
    void setFirstName(String firstName);

    String getLastName();
    void setLastName(String lastName);

    String getPassword();
    void setPassword(String password);

    String getEmail();
    void setEmail(String email);

    String getPhoneNumber();
    void setPhoneNumber(String phoneNumber);

    String getAddress();
    void setAddress(String address);

    String getProvider();
    void setProvider(String provider);

    String getProviderId();
    void setProviderId(String providerId);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    /* insert용: 모든 값 복사 */
    default void copyMembers(IMember iMember) {
        setId(iMember.getId());
        setFirstName(iMember.getFirstName());
        setLastName(iMember.getLastName());
        setEmail(iMember.getEmail());
        setPhoneNumber(iMember.getPhoneNumber());
        setPassword(iMember.getPassword());
        setAddress(iMember.getAddress());
        setProvider(iMember.getProvider());
        setProviderId(iMember.getProviderId());
        setCreatedAt(iMember.getCreatedAt());
        setUpdatedAt(iMember.getUpdatedAt());

    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullMembers(IMember iMember) {
        if (iMember.getId() != null) setId(iMember.getId());
        if (iMember.getFirstName() != null) setFirstName(iMember.getFirstName());
        if (iMember.getLastName() != null) setLastName(iMember.getLastName());
        if (iMember.getEmail() != null) setEmail(iMember.getEmail());
        if (iMember.getPhoneNumber() != null) setPhoneNumber(iMember.getPhoneNumber());
        if  (iMember.getPassword() != null) setPassword(iMember.getPassword());
        if (iMember.getAddress() != null) setAddress(iMember.getAddress());
        if (iMember.getProvider() != null) setProvider(iMember.getProvider());
        if (iMember.getProviderId() != null) setProviderId(iMember.getProviderId());
        if (iMember.getUpdatedAt() != null) setUpdatedAt(iMember.getUpdatedAt());
    }
}
