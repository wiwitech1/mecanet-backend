package com.wiwitech.mecanetbackend.workorders.interfaces.rest;

import com.wiwitech.mecanetbackend.workorders.domain.services.WorkOrderCommandService;
import com.wiwitech.mecanetbackend.workorders.domain.services.WorkOrderQueryService;
import com.wiwitech.mecanetbackend.workorders.domain.model.queries.*;
import com.wiwitech.mecanetbackend.workorders.domain.model.commands.*;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderId;
import com.wiwitech.mecanetbackend.workorders.domain.model.valueobjects.WorkOrderStatus;
import com.wiwitech.mecanetbackend.workorders.domain.model.aggregates.WorkOrder;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.WorkOrderRequestResource;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.resources.WorkOrderResponseResource;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform.CreateWorkOrderCommandFromResourceAssembler;
import com.wiwitech.mecanetbackend.workorders.interfaces.rest.transform.WorkOrderResourceFromEntityAssembler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/workorders")
@RequiredArgsConstructor
@Validated
@Tag(name = "WorkOrders", description = "Work Order Management Endpoints")
public class WorkOrdersController {
    private final WorkOrderCommandService commandService;
    private final WorkOrderQueryService queryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get work order by ID", description = "Retrieve a work order by its unique identifier.")
    public ResponseEntity<WorkOrderResponseResource> getById(@PathVariable Long id) {
        return queryService.handle(new GetWorkOrderByIdQuery(new WorkOrderId(id)))
            .map(WorkOrderResourceFromEntityAssembler::toResource)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get work orders by status", description = "Retrieve all work orders filtered by status. If no status is provided, returns all work orders.")
    public List<WorkOrderResponseResource> getByStatus(@RequestParam(required = false) WorkOrderStatus status) {
        List<WorkOrder> list = (status != null)
            ? queryService.handle(new GetWorkOrdersByStatusQuery(status))
            : queryService.handle(new GetWorkOrdersByStatusQuery(null));
        return list.stream().map(WorkOrderResourceFromEntityAssembler::toResource).toList();
    }

    @PostMapping
    @Operation(summary = "Create work order", description = "Create a new work order.")
    public ResponseEntity<WorkOrderResponseResource> create(@Valid @RequestBody WorkOrderRequestResource resource) {
        var cmd = CreateWorkOrderCommandFromResourceAssembler.toCommand(resource);
        WorkOrder wo = commandService.handle(cmd);
        return ResponseEntity.ok(WorkOrderResourceFromEntityAssembler.toResource(wo));
    }

    @PostMapping("/{id}/schedule")
    @Operation(summary = "Define work order schedule", description = "Define the schedule and maximum number of technicians for a work order.")
    public ResponseEntity<WorkOrder> defineSchedule(@PathVariable Long id, @RequestBody DefineScheduleCommand cmd) {
        WorkOrder wo = commandService.handle(new DefineScheduleCommand(new WorkOrderId(id), cmd.schedule(), cmd.maxTechnicians()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish work order", description = "Publish a work order, making it available for technicians to join.")
    public ResponseEntity<WorkOrder> publish(@PathVariable Long id, @RequestBody PublishWorkOrderCommand cmd) {
        WorkOrder wo = commandService.handle(new PublishWorkOrderCommand(new WorkOrderId(id), cmd.adminUserId()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join work order", description = "Assign a technician to a published work order.")
    public ResponseEntity<WorkOrder> join(@PathVariable Long id, @RequestBody JoinWorkOrderCommand cmd) {
        WorkOrder wo = commandService.handle(new JoinWorkOrderCommand(new WorkOrderId(id), cmd.technicianId()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/leave")
    @Operation(summary = "Leave work order", description = "Remove a technician from a published work order.")
    public ResponseEntity<WorkOrder> leave(@PathVariable Long id, @RequestBody LeaveWorkOrderCommand cmd) {
        WorkOrder wo = commandService.handle(new LeaveWorkOrderCommand(new WorkOrderId(id), cmd.technicianId(), cmd.reason()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/materials")
    @Operation(summary = "Update work order materials", description = "Update the list of materials required for a work order.")
    public ResponseEntity<WorkOrder> updateMaterials(@PathVariable Long id, @RequestBody UpdateMaterialsCommand cmd) {
        WorkOrder wo = commandService.handle(new UpdateMaterialsCommand(new WorkOrderId(id), cmd.materials()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/review")
    @Operation(summary = "Move work order to review", description = "Move a work order to the review state.")
    public ResponseEntity<WorkOrder> moveToReview(@PathVariable Long id, @RequestBody MoveWorkOrderToReviewCommand cmd) {
        WorkOrder wo = commandService.handle(new MoveWorkOrderToReviewCommand(new WorkOrderId(id), cmd.adminUserId()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/pending-execution")
    @Operation(summary = "Set work order as pending execution", description = "Set a work order to the pending execution state.")
    public ResponseEntity<WorkOrder> setPendingExecution(@PathVariable Long id, @RequestBody SetPendingExecutionCommand cmd) {
        WorkOrder wo = commandService.handle(new SetPendingExecutionCommand(new WorkOrderId(id), cmd.adminUserId()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start work order execution", description = "Start the execution of a work order.")
    public ResponseEntity<WorkOrder> start(@PathVariable Long id, @RequestBody StartWorkOrderCommand cmd) {
        WorkOrder wo = commandService.handle(new StartWorkOrderCommand(new WorkOrderId(id), cmd.technicianId(), cmd.startAt()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete work order", description = "Mark a work order as completed.")
    public ResponseEntity<WorkOrder> complete(@PathVariable Long id, @RequestBody CompleteWorkOrderCommand cmd) {
        WorkOrder wo = commandService.handle(new CompleteWorkOrderCommand(new WorkOrderId(id), cmd.technicianId(), cmd.endAt(), cmd.conclusions()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/comment")
    @Operation(summary = "Add comment to work order", description = "Add a comment to a work order during execution.")
    public ResponseEntity<WorkOrder> addComment(@PathVariable Long id, @RequestBody AddCommentCommand cmd) {
        WorkOrder wo = commandService.handle(new AddCommentCommand(new WorkOrderId(id), cmd.authorUserId(), cmd.text()));
        return ResponseEntity.ok(wo);
    }

    @PostMapping("/{id}/photo")
    @Operation(summary = "Add photo to work order", description = "Add a photo to a work order during execution.")
    public ResponseEntity<WorkOrder> addPhoto(@PathVariable Long id, @RequestBody AddPhotoCommand cmd) {
        WorkOrder wo = commandService.handle(new AddPhotoCommand(new WorkOrderId(id), cmd.authorUserId(), cmd.url()));
        return ResponseEntity.ok(wo);
    }
} 