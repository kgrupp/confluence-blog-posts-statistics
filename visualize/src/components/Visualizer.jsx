'use client';

import React from "react";
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import statistics from "@/data/statistics.json";
import Typography from "@mui/material/Typography";
import Chip from "@mui/material/Chip";
import ArticleIcon from '@mui/icons-material/Article';
import RecommendIcon from '@mui/icons-material/Recommend';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import Link from "@mui/material/Link";

export const Visualizer = () => {
    const totalBlogPosts = statistics.reduce((acc, statistic) => acc + statistic.totalBlogPosts, 0);
    const totalLikes = statistics.reduce((acc, statistic) => acc + statistic.totalLikes, 0);
    return <Box sx={{display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: 1}}>
        <Box sx={{display: 'flex', justifyContent: 'center', gap: 1, paddingBottom: 1, alignItems: 'center'}}>
            <Chip icon={<ArticleIcon/>} label={totalBlogPosts}/>
            <Chip icon={<RecommendIcon/>} label={totalLikes}/>
        </Box>
        <Grid sx={{
            display: 'flex',
            flexDirection: 'row',
            flexWrap: 'wrap',
            justifyContent: 'center',
            gap: 1
        }}>{statistics.map((statistic, index) => (
            <Card variant={"elevation"} key={statistic.user.id} sx={{width: 500}}>
                <CardContent>
                    <Typography variant="h5" component="div" sx={{display: 'flex', justifyContent: 'space-between'}}>
                        <Box>{index === 0 && <EmojiEventsIcon color={'primary'}/>}{statistic.user.name}</Box>
                        <Box sx={{display: 'flex', gap: 1}}>
                            <Chip icon={<ArticleIcon/>} label={statistic.totalBlogPosts}/>
                            <Chip icon={<RecommendIcon/>} label={statistic.totalLikes}/>
                        </Box>
                    </Typography>
                    <Typography variant="body2" sx={{display: 'flex', flexDirection: 'column', gap: 1, paddingTop: 1}}>
                        {statistic.popularBlogPosts.map(blogPost => (
                            <Box key={blogPost.title} sx={{display: 'flex', justifyContent: 'space-between'}}>
                                <Link href={blogPost.link} underline={"none"} target={"_blank"}>{blogPost.title}</Link>
                                {blogPost.likeCount > 0 &&
                                    <Chip size={'small'} sx={{marginLeft: 1}} icon={<RecommendIcon/>}
                                          label={blogPost.likeCount}/>}
                            </Box>))}
                    </Typography>
                </CardContent>
            </Card>
        ))}</Grid>
    </Box>;
};
