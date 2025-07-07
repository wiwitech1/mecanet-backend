package com.wiwitech.mecanetbackend.workorders.domain.model.queries;

import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.UserId;
 
/**
 * Query to get a technician by IAM user ID.
 */
public record GetTechnicianByIamUserIdQuery(UserId iamUserId) {} 