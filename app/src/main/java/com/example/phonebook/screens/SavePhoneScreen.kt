package com.example.phonebook.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.phonebook.routing.PhoneBookRouter
import com.example.phonebook.routing.Screen
import com.example.phonebook.viewmodel.MainViewModel
import com.example.phonebook.R
import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SavePhoneScreen(viewModel: MainViewModel) {

    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val phoneEntry by viewModel.phoneEntry.observeAsState(PhoneModel())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val movePhoneToTrashDialogShownState = rememberSaveable { mutableStateOf(false)}

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            PhoneBookRouter.navigateTo(Screen.Phones)
        }
    }

    Scaffold (
        topBar = {
            val isEdittingMode: Boolean = phoneEntry.id != NEW_PHONE_ID
            SavePhoneTopBar(
                isEditiingMode = isEdittingMode,
                onBackClick = { PhoneBookRouter.navigateTo(Screen.Phones) },
                onSavePhoneClick = { viewModel.savePhone(phoneEntry) },
                onOpenTagPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeletePhoneClick = {
                    movePhoneToTrashDialogShownState.value = true
                }
            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                TagPicker(
                    tags = tags,
                    onTagSelect = { tag ->
                        viewModel.onPhoneEntryChange(phoneEntry.copy(tag = tag))
                    }
                )
            }
        ) {
            SavePhoneContent(
                phone = phoneEntry,
                onPhoneChange = { updatePhoneEntry ->
                    viewModel.onPhoneEntryChange(updatePhoneEntry)
                }
            )
        }

        if (movePhoneToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    movePhoneToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move phone number to the trash?")
                },
                text = {
                    Text(
                        "Are you sure you want to " +
                                "move this phone number to the trash?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.movePhoneToTrash(phoneEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        movePhoneToTrashDialogShownState.value = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}

@Composable
fun SavePhoneTopBar(
    isEditiingMode: Boolean,
    onBackClick: () -> Unit,
    onSavePhoneClick: () -> Unit,
    onOpenTagPickerClick: () -> Unit,
    onDeletePhoneClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Phone Number",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, 
                    contentDescription = "Back",
                    tint = MaterialTheme.colors.onPrimary )
                
            }
        },
        actions = {
            IconButton(onClick = onSavePhoneClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Phone Number",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            
            IconButton(onClick = onOpenTagPickerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.hashtag),
                    contentDescription = "Open Tag Picker Button",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
            if (isEditiingMode) {
                IconButton(onClick = onDeletePhoneClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Phone Number Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SavePhoneContent(
    phone: PhoneModel,
    onPhoneChange: (PhoneModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        PhoneTextField(
            label = "Name",
            text = phone.name,
            onTextChange = { newName ->
                onPhoneChange.invoke(phone.copy(name = newName))
            }
        )

        PhoneTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Phone Number",
            text = phone.phoneNumber,
            onTextChange = { newPhoneNumber ->
                onPhoneChange.invoke(phone.copy(phoneNumber = newPhoneNumber))
            }
        )
        PickedTag(tag = phone.tag)
    }
}

@Composable
private fun PhoneTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@Composable
private fun PhoneCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can phone number be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun PickedTag(tag: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked tag",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = 8.dp)
        )
        Text(
            text = tag.nameTag,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TagPicker(
    tags: List<TagModel>,
    onTagSelect: (TagModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tag picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tags.size) { itemIndex ->
                val tag = tags[itemIndex]
                TagItem(
                    tag = tag,
                    onTagSelect = onTagSelect
                )
            }
        }
    }
}

@Composable
fun TagItem(
    tag: TagModel,
    onTagSelect: (TagModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onTagSelect(tag)
                }
            )
    ) {
        Text(
            text = tag.nameTag,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun TagItemPreview() {
    TagItem(TagModel.DEFAULT) {}
}

@Preview
@Composable
fun TagPickerPreview() {
    TagPicker(
        tags = listOf(
            TagModel.DEFAULT,
            TagModel.DEFAULT,
            TagModel.DEFAULT,
        )
    ) { }
}


@Preview
@Composable
fun PickedTagPreview() {
    PickedTag(TagModel.DEFAULT)
}