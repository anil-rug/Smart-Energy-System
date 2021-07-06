import React from 'react';
import { Route } from 'react-router-dom';
import { render } from 'react-dom';
import { ResponsiveBar } from '@nivo/bar';
import { ResponsivePie } from '@nivo/pie'
import { Container, Row, Col } from 'react-bootstrap';
import { MainBody, DashboardContainer } from './dashboardStyles';
import axios from 'axios';

var jsonResponse = [
    {
        "appliance": "Heating & AC",
        "watt": 34
    },
    {
        "appliance": "EV Charge",
        "watt": 184
    },
    {
        "appliance": "Plug Loads",
        "watt": 57
    },
    {
        "appliance": "Refrigeration",
        "watt": 29
    },
    {
        "appliance": "Others",
        "watt": 89
    }
]

var pieJson = [
    {
        "id": "ac",
        "label": "Heating & AC",
        "value": 321,
        "color": "hsl(219, 70%, 50%)"
    },
    {
        "id": "ev",
        "label": "EV Charge",
        "value": 467,
        "color": "hsl(218, 70%, 50%)"
    },
    {
        "id": "refrigeration",
        "label": "Refrigeration",
        "value": 133,
        "color": "hsl(126, 70%, 50%)"
    }
]


class Dashboard extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            energyResult: []
        }
    }
    componentDidMount() {
        axios.get('https://demo1599494.mockable.io/EnergyConsumption').then(respone => {
            console.log(respone);
            this.setState({ energyResult: respone.data })
        }).catch(error => {
            console.log(error);
        })

    }

    render() {
        return (
            <MainBody>
                {/* <Container> */}
                <Row>
                    <Col style={{ height: 'auto', width: 'auto' }}>
                        <ResponsiveBar
                            data={this.state.energyResult}
                            keys={['watt']}
                            indexBy="appliance"
                            margin={{ top: 50, right: 50, bottom: 50, left: 100 }}
                            padding={0.2}
                            layout="horizontal"
                            colors={{ scheme: 'category10' }}
                            borderRadius={2}
                            borderWidth={2}
                            borderColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
                            axisTop={null}
                            axisRight={null}
                            axisBottom={null}
                            axisLeft={{
                                tickSize: 0,
                                tickPadding: 4,
                                tickRotation: 0,
                                // legend: 'food',
                                legendPosition: 'middle',
                                // legendOffset: -20
                            }}
                            // axisLeft={null}
                            labelSkipWidth={12}
                            labelSkipHeight={12}
                            labelTextColor={{ from: 'color', modifiers: [['darker', 5.6]] }}
                            // legends={[
                            //     {
                            //         dataFrom: 'keys',
                            //         anchor: 'bottom-right',
                            //         direction: 'column',
                            //         justify: false,
                            //         translateX: 120,
                            //         translateY: 0,
                            //         itemsSpacing: 2,
                            //         itemWidth: 100,
                            //         itemHeight: 20,
                            //         itemDirection: 'left-to-right',
                            //         itemOpacity: 0.85,
                            //         symbolSize: 20,
                            //         effects: [
                            //             {
                            //                 on: 'hover',
                            //                 style: {
                            //                     itemOpacity: 1
                            //                 }
                            //             }
                            //         ]
                            //     }
                            // ]}
                            animate={true}
                            motionStiffness={90}
                            motionDamping={15}
                        />
                    </Col>
                    <Col style={{ height: 300, width: '100%' }}>
                        <ResponsivePie
                            data={pieJson}
                            margin={{ top: 40, right: 80, bottom: 80, left: 80 }}
                            innerRadius={0.5}
                            padAngle={0.7}
                            cornerRadius={3}
                            colors={{ scheme: 'category10' }}
                            borderWidth={1}
                            borderColor={{ from: 'color', modifiers: [['darker', 0.2]] }}
                            enableRadialLabels={false}
                            radialLabelsSkipAngle={10}
                            radialLabelsTextXOffset={6}
                            radialLabelsTextColor="#333333"
                            radialLabelsLinkOffset={0}
                            radialLabelsLinkDiagonalLength={16}
                            radialLabelsLinkHorizontalLength={24}
                            radialLabelsLinkStrokeWidth={1}
                            radialLabelsLinkColor={{ from: 'color' }}
                            slicesLabelsSkipAngle={10}
                            slicesLabelsTextColor="#333333"
                            animate={true}
                            motionStiffness={90}
                            motionDamping={15}
                            legends={[
                                {
                                    anchor: 'bottom',
                                    direction: 'row',
                                    translateY: 56,
                                    itemWidth: 100,
                                    itemHeight: 18,
                                    itemTextColor: '#999',
                                    symbolSize: 18,
                                    symbolShape: 'circle',
                                    effects: [
                                        {
                                            on: 'hover',
                                            style: {
                                                itemTextColor: '#000'
                                            }
                                        }
                                    ]
                                }
                            ]}
                        />
                    </Col>
                </Row>
                {/* </Container> */}
            </MainBody>
        );
    }
}

export default Dashboard