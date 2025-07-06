package com.wiwitech.mecanetbackend.workorders.domain.services;

import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.*;

/**
 * Domain service that processes WorkOrder-related commands.
 */
public interface WorkOrderCommandService {
    WorkOrder handle(CreateWorkOrderCommand command);
    WorkOrder handle(DefineScheduleCommand command);
    WorkOrder handle(PublishWorkOrderCommand command);
    WorkOrder handle(MoveWorkOrderToReviewCommand command);
    WorkOrder handle(SetPendingExecutionCommand command);
    WorkOrder handle(JoinWorkOrderCommand command);
    WorkOrder handle(LeaveWorkOrderCommand command);
    WorkOrder handle(UpdateMaterialsCommand command);
    WorkOrder handle(StartWorkOrderCommand command);
    WorkOrder handle(CompleteWorkOrderCommand command);
    WorkOrder handle(AddCommentCommand command);
    WorkOrder handle(AddPhotoCommand command);
} 