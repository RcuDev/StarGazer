package com.rcudev.posts.ui.posts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rcudev.ds.theme.Typography
import com.rcudev.posts.domain.model.Post
import com.rcudev.posts.ui.ViewState
import com.rcudev.utils.LocalImageLoader
import com.rcudev.utils.logMessage

@Stable
@Composable
internal fun PostContent(
    viewState: ViewState,
    loadNextPage: () -> Unit,
    showSnackBar: () -> Unit,
    onItemClick: (String) -> Unit,
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

@Stable
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(
            items = posts,
            key = { _, post -> post.id }
        ) { index, post ->
            ItemImpression(
                index = index,
                listState = listState,
                onItemViewed = {
                    endReached.value = post.id == posts.last().id
                }
            )
            PostItem(
                post = post,
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

@Stable
@Composable
private fun PostItem(
    post: Post,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onItemClick(post.url)
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            PostImage(post = post)
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = post.title,
                style = Typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.newsSite,
                    style = Typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = post.publishedAt,
                    style = Typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = post.displaySummary,
                style = Typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Stable
@Composable
private fun PostImage(
    post: Post,
    modifier: Modifier = Modifier
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
                logMessage("PostImage", it.result.throwable.message ?: "unknown")
                error = true
            },
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Stable
@Composable
private fun ItemImpression(
    index: Int,
    listState: LazyListState,
    onItemViewed: () -> Unit
) {
    val isItemInView by remember {
        derivedStateOf {
            listState.layoutInfo
                .visibleItemsInfo
                .any { it.index == index }
        }
    }
    
    if (isItemInView) {
        LaunchedEffect(Unit) { 
            onItemViewed() 
        }
    }
}