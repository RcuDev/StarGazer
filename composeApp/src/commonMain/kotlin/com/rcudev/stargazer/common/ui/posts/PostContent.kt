package com.rcudev.stargazer.common.ui.posts

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rcudev.ds.theme.Typography
import com.rcudev.stargazer.LocalImageLoader
import com.rcudev.stargazer.common.di.ViewState
import com.rcudev.stargazer.common.domain.model.Post

@Composable
internal fun PostContent(
    viewState: ViewState,
    loadNextPage: () -> Unit,
    showSnackBar: () -> Unit,
    onItemClick: (String) -> Unit
) {
    when (viewState) {
        ViewState.Loading -> Loading()
        ViewState.Error -> Error()
        is ViewState.Empty -> Empty(viewState.message)
        is ViewState.Success -> Success(
            posts = viewState.posts,
            loadingNextPage = viewState.loadingNextPage,
            loadNextPage = loadNextPage,
            showSnackBar = showSnackBar,
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun Loading() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun Empty(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = message)
    }
}

@Composable
private fun Error() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Error")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Success(
    posts: List<Post>,
    loadingNextPage: Boolean = false,
    loadNextPage: () -> Unit,
    showSnackBar: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val listState = rememberLazyListState()
    val endReached = remember { mutableStateOf(false) }

    LazyColumn(
        state = listState,
    ) {
        itemsIndexed(
            items = posts,
            key = { index, article -> "$index-$article" }) { index, article ->
            ItemImpression(
                index = index,
                listState = listState,
                onItemViewed = {
                    endReached.value = article.id == posts.last().id
                }
            )
            Post(
                post = article,
                onItemClick = onItemClick
            )
        }
    }

    if (endReached.value) {
        LaunchedEffect(Unit) {
            if (!loadingNextPage) {
                loadNextPage()
                showSnackBar()
            }
        }
    }
}

@Composable
private fun Post(
    modifier: Modifier = Modifier,
    post: Post,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .clickable(
                onClick = { onItemClick(post.url) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        PostImage(
            post
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = post.title,
            style = Typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = post.summary,
            style = Typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = post.newsSite,
                style = Typography.labelSmall,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = post.publishedAt,
                style = Typography.labelSmall,
            )
        }
    }
}

@Composable
private fun PostImage(
    post: Post
) {
    val imageLoader = LocalImageLoader.current
    var error by remember { mutableStateOf(false) }

    if (!error) {
        AsyncImage(
            imageLoader = imageLoader,
            model = post.imageUrl,
            contentDescription = post.title,
            contentScale = ContentScale.Crop,
            onError = {
                error = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Composable
private fun ItemImpression(index: Int, listState: LazyListState, onItemViewed: () -> Unit) {
    val isItemWithKeyInView by remember {
        derivedStateOf {
            listState.layoutInfo
                .visibleItemsInfo
                .any { it.index == index }
        }
    }
    if (isItemWithKeyInView) {
        LaunchedEffect(Unit) { onItemViewed() }
    }
}