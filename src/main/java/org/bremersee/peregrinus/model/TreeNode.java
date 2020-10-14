package org.bremersee.peregrinus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.util.List;

public abstract class TreeNode {

  /**
   * Unique key of the node.
   */
  @JsonProperty(value = "key", access = Access.READ_ONLY)
  private String id;

  /**
   * Label of the node.
   */
  private String label;

  /**
   * Icon of the node to display next to content.
   */
  private String icon;

  /**
   * Icon to use in expanded state.
   */
  private String expandedIcon; // can be any

  /**
   * Icon to use in collapsed state.
   */
  private String collapsedIcon; // can be any

  /**
   * An array of treenodes as children.
   */
  private List<TreeNode> children;

  /**
   * Specifies if the node has children. Used in lazy loading.
   */
  private Boolean leaf;

//  /**
//   * Inline style of the node.
//   */
//  private String style;

  /**
   * Style class of the node.
   */
  private String styleClass;

  /**
   * Whether the node is in an expanded or collapsed state.
   */
  private Boolean expanded;

  /**
   * Type of the node to match ng-template type.
   */
  private String type;

  /**
   * Parent of the node.
   */
  private TreeNode parent;

  /**
   * Whether the node is partial selected.
   */
  private Boolean partialSelected;

  /**
   * Whether to disable dragging for a particular node even if draggableNodes is enabled.
   */
  private Boolean draggable;

  /**
   * Whether to disable dropping for a particular node even if droppableNodes is enabled.
   */
  private Boolean droppable;

  /**
   * Used to disable selection of a particular node.
   */
  private Boolean selectable;

  /**
   * Data represented by the node.
   */
  private String data;

}
