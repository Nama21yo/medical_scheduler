sealed class AddEmployeeEvent {
    data class OnNameChanged(val name: String) : AddEmployeeEvent()
    data class OnEmailChanged(val email: String) : AddEmployeeEvent()
    data class OnBranchIdChanged(val branchId: Int) : AddEmployeeEvent()
    data class OnRoleSelected(val role: String) : AddEmployeeEvent()
    object ToggleRoleDropdown : AddEmployeeEvent()
    object DismissRoleDropdown : AddEmployeeEvent()
    object OnAddEmployeeClicked : AddEmployeeEvent()
    object ResetSuccessState : AddEmployeeEvent()
}
